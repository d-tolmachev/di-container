package team.zavod.di.util;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
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
  private final List<ClassLoader> classLoaders;
  private final Set<URL> urls;

  public ClasspathHelper(String name) {
    this.typesToSubTypes = new HashMap<>();
    this.classLoaders = getDefaultClassLoaders();
    this.urls = new HashSet<>();
    setUrlsForPackage(name);
    scan();
  }

  public <T> Set<Class<? extends T>> getSubTypesOf(Class<T> type) {
    return Set.copyOf(listClassesForNames(this.typesToSubTypes.get(type.getName())));
  }

  private void setUrlsForPackage(String name) {
    String resourceName = name.replace(".", "/");
    for (ClassLoader classLoader : this.classLoaders) {
      try {
        Enumeration<URL> resources = classLoader.getResources(resourceName);
        while (resources.hasMoreElements()) {
          URL resource = resources.nextElement();
          this.urls.add(new URL(resource.toExternalForm().substring(0, resource.toExternalForm().lastIndexOf(resourceName))));
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void scan() {
    this.urls.forEach(url -> getFiles(new File(url.getFile())).forEach(this::scan));
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
  }

  @SuppressWarnings("unchecked")
  private <T> List<Class<? extends T>> listClassesForNames(Collection<String> classes) {
    return classes.stream()
        .<Class<? extends T>>map(className -> (Class<? extends T>) classForName(className))
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  private List<ClassFile> getFiles(File directory) {
    List<ClassFile> files = new ArrayList<>();
    if (!directory.exists()) {
      return files;
    }
    try {
      for (File file : Objects.requireNonNull(directory.listFiles())) {
        if (file.isDirectory()) {
          files.addAll(getFiles(file));
        } else if (file.getName().endsWith(".class")) {
          files.add(new ClassFile(new DataInputStream(new BufferedInputStream(new FileInputStream(file)))));
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return files;
  }

  private Class<?> classForName(String typeName) {
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

  private List<ClassLoader> getDefaultClassLoaders() {
    return List.of(Thread.currentThread().getContextClassLoader(), getClass().getClassLoader());
  }
}
