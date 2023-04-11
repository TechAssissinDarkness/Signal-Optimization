package org.signal.openapi;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeBindings;
import io.dropwizard.auth.Auth;
import io.swagger.v3.jaxrs2.ResolvedParameter;
import io.swagger.v3.jaxrs2.ext.AbstractOpenAPIExtension;
import io.swagger.v3.oas.models.Components;
import org.whispersystems.textsecuregcm.auth.AuthenticatedAccount;
import org.whispersystems.textsecuregcm.auth.DisabledPermittedAuthenticatedAccount;

import javax.ws.rs.Consumes;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class OpenApiExtension extends AbstractOpenAPIExtension {

    private static final Class<Auth> AUTH_ANNOTATION_CLASS = Auth.class;
    private static final ResolvedParameter AUTHENTICATED_ACCOUNT = new ResolvedParameter();
    private static final ResolvedParameter OPTIONAL_AUTHENTICATED_ACCOUNT = new ResolvedParameter();
    private static final ResolvedParameter DISABLED_PERMITTED_AUTHENTICATED_ACCOUNT = new ResolvedParameter();
    private static final ResolvedParameter OPTIONAL_DISABLED_PERMITTED_AUTHENTICATED_ACCOUNT = new ResolvedParameter();

    @Override
    public ResolvedParameter extractParameters(
            List<Annotation> annotations,
            Type type,
            Set<Type> typesToSkip,
            Components components,
            Consumes classConsumes,
            Consumes methodConsumes,
            boolean includeRequestBody,
            JsonView jsonViewAnnotation,
            Iterator<OpenAPIExtension> chain) {

        if (annotations.stream().anyMatch(a -> a.annotationType().equals(AUTH_ANNOTATION_CLASS))) {
            Optional<Class<?>> rawClass = extractRawClass(type);
            if (rawClass.isPresent()) {
                if (AuthenticatedAccount.class.isAssignableFrom(rawClass.get())) {
                    return AUTHENTICATED_ACCOUNT;
                } else if (DisabledPermittedAuthenticatedAccount.class.isAssignableFrom(rawClass.get())) {
                    return DISABLED_PERMITTED_AUTHENTICATED_ACCOUNT;
                } else if (Optional.class.isAssignableFrom(rawClass.get())) {
                    Optional<Class<?>> parameterType = extractParameterType(type);
                    if (parameterType.isPresent() && AuthenticatedAccount.class.isAssignableFrom(parameterType.get())) {
                        return OPTIONAL_AUTHENTICATED_ACCOUNT;
                    } else if (parameterType.isPresent()
                            && DisabledPermittedAuthenticatedAccount.class.isAssignableFrom(parameterType.get())) {
                        return OPTIONAL_DISABLED_PERMITTED_AUTHENTICATED_ACCOUNT;
                    }
                }
            }
        }

        return super.extractParameters(
                annotations,
                type,
                typesToSkip,
                components,
                classConsumes,
                methodConsumes,
                includeRequestBody
