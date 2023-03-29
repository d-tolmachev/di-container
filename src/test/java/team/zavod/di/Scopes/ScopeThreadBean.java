package team.zavod.di.Scopes;

import team.zavod.di.annotation.Component;
import team.zavod.di.annotation.Scope;

@Component
@Scope(value = "thread")
public class ScopeThreadBean {
}
