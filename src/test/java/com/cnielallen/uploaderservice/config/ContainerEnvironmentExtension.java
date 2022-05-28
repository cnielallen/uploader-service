package com.cnielallen.uploaderservice.config;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.*;

@Slf4j
public class ContainerEnvironmentExtension implements ParameterResolver, BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {

    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext context) throws ParameterResolutionException {
        return Environment.class.equals(parameterContext.getParameter().getType());
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext context) throws ParameterResolutionException {
       ExtensionContext engineContext = context.getRoot();
       ExtensionContext.Store store = engineContext.getStore(ExtensionContext.Namespace.GLOBAL);
       return store.getOrComputeIfAbsent(ContainerEnvironmentResource.class);
    }
}
