package tn.smarthealth.graphqldashboard.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GraphQlExceptionAdvice {

    @GraphQlExceptionHandler
    public GraphQLError notFound(ResourceNotFoundException ex, DataFetchingEnvironment env) {
        return GraphqlErrorBuilder.newError(env).errorType(ErrorType.NOT_FOUND).message(ex.getMessage()).build();
    }

    @GraphQlExceptionHandler
    public GraphQLError badRequest(BadRequestException ex, DataFetchingEnvironment env) {
        return GraphqlErrorBuilder.newError(env).errorType(ErrorType.BAD_REQUEST).message(ex.getMessage()).build();
    }
}