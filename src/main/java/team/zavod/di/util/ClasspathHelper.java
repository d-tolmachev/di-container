package team.zavod.di.util;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;

public class ClasspathHelper {
  private static final Map<String, Class<?>> PRIMITIVE_NAMES_TO_TYPES = Map.of(
      "boolean", boolean.class,
      "char", char.class,
      "byte", byte.class,
      "short", short.class,
      "int", int.class,
      "long", long.class,
      "float", float.class,
      "double", double.class,
      "void", void.class);
  private static final Map<String, String> PRIMITIVE_NAMES_TO_DESCRIPTORS = Map.of(
      "boolean", "Z",
      "char", "C",
      "byte", "B",
      "short", "S",
      "int", "I",
      "long", "J",
      "float", "F",
      "double", "D",
      "void", "V");
  private final Map<String, Set<String>> typesToSubTypes;
  private final Map<String, Set<String>> annotationsToTypes;
  private final Set<ClassLoader> classLoaders;
  private final Map<URI, String> uriToRelativePath;

  public ClasspathHelper(String name) {
    this(List.of(name));
  }

  public ClasspathHelper(List<String> names) {
    this.typesToSubTypes = new HashMap<>();
    this.annotationsToTypes = new HashMap<>();
    this.classLoaders = new HashSet<>();
    this.uriToRelativePath = new HashMap<>();
    this.classLoaders.add(getDefaultClassLoader());
    names.forEach(this::setUrlsForPackage);
    scan();
  }

  public ClasspathHelper(String name, ClassLoader classLoader) {
    this(name);
    this.classLoaders.add(classLoader);
  }

  public ClasspathHelper(List<String> names, ClassLoader classLoader) {
    this(names);
    this.classLoaders.add(classLoader);
  }

  public Class<?> classForName(String typeName) {
    if (PRIMITIVE_NAMES_TO_TYPES.containsKey(typeName)) {
      return PRIMITIVE_NAMES_TO_TYPES.get(typeName);
    }
    String type;
    if (typeName.contains("[")) {
      type = typeName.substring(0, typeName.indexOf("["));
      type = PRIMITIVE_NAMES_TO_DESCRIPTORS.getOrDefault(type, "L" + type);
      type = typeName.substring(typeName.indexOf("[")).replace("]", "") + type;
    } else type = typeName;
    for (ClassLoader classLoader : this.classLoaders) {
      try {
        return Class.forName(type, false, classLoader);
      } catch (ClassNotFoundException ignored) {
      }
    }
    return null;
  }

  public <T> Set<Class<? extends T>> getSubTypesOf(Class<T> type) {
    return Set.copyOf(listClassesForNames(getSubTypesOf(type.getName())));
  }

  public Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation> annotation) {
    return Set.copyOf(listClassesForNames(getTypesAnnotatedWith(annotation.getName())));
  }

  private ClassLoader getDefaultClassLoader() {
    return Thread.currentThread().getContextClassLoader();
  }

  private void setUrlsForPackage(String name) {
    String resourceName = name.replace(".", "/");
    for (ClassLoader classLoader : this.classLoaders) {
      try {
        Enumeration<URL> resources = classLoader.getResources(resourceName);
        while (resources.hasMoreElements()) {
          URL resource = resources.nextElement();
          String resourceUrl = Objects.nonNull(resource.toURI().getPath()) ? resource.getPath().substring(1) : resourceName;
          String resourcePath = Objects.nonNull(resource.toURI().getPath()) ? resource.toURI().getPath().substring(1) : resourceName;
          this.uriToRelativePath.put(new URI(resource.toExternalForm().substring(0, resource.toExternalForm().lastIndexOf(resourceUrl))), resourcePath);
        }
      } catch (IOException | URISyntaxException e) {
        e.printStackTrace();
      }
    }
  }

  private void scan() {
    for (Entry<URI, String> entry : this.uriToRelativePath.entrySet()) {
      try (FileSystem fileSystem = getFileSystem(entry.getKey())) {
        getFiles(fileSystem.getPath(entry.getValue())).forEach(this::scan);
      } catch (UnsupportedOperationException ignored) {
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void scan(ClassFile classFile) {
    String className = classFile.getName();
    String superclass = classFile.getSuperclass();
    if (!superclass.equals(Object.class.getName())) {
      this.typesToSubTypes.computeIfAbsent(superclass, k -> new HashSet<>()).add(className);
    }
    Arrays.stream(classFile.getInterfaces())
        .filter(anInterface -> !anInterface.equals(Object.class.getName()))
        .forEach(anInterface -> this.typesToSubTypes.computeIfAbsent(anInterface, k -> new HashSet<>()).add(className));
    getClassAnnotationNames(classFile).forEach(annotationType -> this.annotationsToTypes.computeIfAbsent(annotationType, k -> new HashSet<>()).add(className));
  }

  @SuppressWarnings("unchecked")
  private <T> List<Class<? extends T>> listClassesForNames(Collection<String> classes) {
    return classes.stream()
        .<Class<? extends T>>map(className -> (Class<? extends T>) classForName(className))
        .filter(Objects::nonNull)
        .toList();
  }

  private Set<String> getSubTypesOf(String type) {
    Set<String> subTypes = new HashSet<>();
    if (this.typesToSubTypes.containsKey(type)) {
      subTypes.addAll(this.typesToSubTypes.get(type));
      this.typesToSubTypes.get(type).stream()
          .filter(this.typesToSubTypes::containsKey)
          .forEach(subType -> subTypes.addAll(getSubTypesOf(subType)));
    }
    return subTypes;
  }

  private Set<String> getTypesAnnotatedWith(String annotation) {
    Set<String> types = new HashSet<>();
    if (this.annotationsToTypes.containsKey(annotation)) {
      types.addAll(this.annotationsToTypes.get(annotation));
    }
    return types;
  }

  private List<ClassFile> getFiles(Path directory) {
    List<ClassFile> files = new ArrayList<>();
    if (!Files.isDirectory(directory)) {
      return files;
    }
    try (DirectoryStream<Path> directories = Files.newDirectoryStream(directory)) {
      for (Path path : directories) {
        if (Files.isDirectory(path)) {
          files.addAll(getFiles(path));
        } else if (path.getFileName().toString().endsWith(".class")) {
          files.add(new ClassFile(new DataInputStream(new BufferedInputStream(Files.newInputStream(path)))));
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return files;
  }

  private FileSystem getFileSystem(URI uri) {
    FileSystem fileSystem = FileSystems.getDefault();
    try {
      try {
        fileSystem = FileSystems.getFileSystem(uri);
      } catch (FileSystemNotFoundException ignored) {
        fileSystem = FileSystems.newFileSystem(uri, Map.of());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return fileSystem;
  }

  private List<String> getClassAnnotationNames(ClassFile classFile) {
    List<String> annotations = new ArrayList<>();
    AnnotationsAttribute annotationsAttribute = (AnnotationsAttribute) classFile.getAttribute(AnnotationsAttribute.visibleTag);
    if (Objects.nonNull(annotationsAttribute)) {
      Arrays.stream(annotationsAttribute.getAnnotations()).forEach(annotation -> annotations.add(annotation.getTypeName()));
    }
    return annotations;
  }
}
