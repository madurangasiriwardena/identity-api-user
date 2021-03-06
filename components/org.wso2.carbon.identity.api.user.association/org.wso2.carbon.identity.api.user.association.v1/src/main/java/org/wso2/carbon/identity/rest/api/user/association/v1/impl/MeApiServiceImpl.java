package org.wso2.carbon.identity.rest.api.user.association.v1.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.api.user.common.ContextLoader;
import org.wso2.carbon.identity.api.user.common.function.UserIdToUser;
import org.wso2.carbon.identity.application.common.model.User;
import org.wso2.carbon.identity.rest.api.user.association.v1.MeApiService;
import org.wso2.carbon.identity.rest.api.user.association.v1.core.UserAssociationService;
import org.wso2.carbon.identity.rest.api.user.association.v1.dto.AssociationUserRequestDTO;
import org.wso2.carbon.user.core.util.UserCoreUtil;

import java.net.URI;
import javax.ws.rs.core.Response;

import static org.wso2.carbon.identity.api.user.common.ContextLoader.buildURI;
import static org.wso2.carbon.identity.rest.api.user.association.v1.AssociationEndpointConstants.ME_CONTEXT;
import static org.wso2.carbon.identity.rest.api.user.association.v1.AssociationEndpointConstants.USER_ASSOCIATIONS_PATH_COMPONENT;
import static org.wso2.carbon.identity.rest.api.user.association.v1.AssociationEndpointConstants.V1_API_PATH_COMPONENT;

/**
 * Association API service implementation for users/me endpoint.
 */
public class MeApiServiceImpl extends MeApiService {

    @Autowired
    private UserAssociationService userAssociationService;

    @Override
    public Response meAssociationsDelete() {

        userAssociationService.deleteUserAccountAssociation(getUserId());
        return Response.noContent().build();
    }

    @Override
    public Response meAssociationsAssociatedUserIdDelete(String associatedUserId) {

        userAssociationService.deleteAssociatedUserAccount(getUserId(), getDecodedUserId(associatedUserId));
        return Response.noContent().build();
    }

    @Override
    public Response meAssociationsGet() {
        return Response.ok().entity(userAssociationService.getAssociationsOfUser(getUserId())).build();
    }

    @Override
    public Response meAssociationsPost(AssociationUserRequestDTO association) {

        userAssociationService.createUserAccountAssociation(association);
        return Response.created(getAssociationsLocationURI()).build();
    }

    @Override
    public Response meFederatedAssociationsGet() {

        return Response.ok().entity(userAssociationService.getFederatedAssociationsOfUser(getUserId())).build();
    }

    @Override
    public Response meFederatedAssociationsDelete() {

        userAssociationService.deleteFederatedUserAccountAssociation(getUserId());
        return Response.noContent().build();
    }

    @Override
    public Response meFederatedAssociationsIdDelete(String id) {

        userAssociationService.deleteFederatedUserAccountAssociation(getUserId(), id);
        return Response.noContent().build();
    }

    private String getUserId() {

        String username = PrivilegedCarbonContext.getThreadLocalCarbonContext().getUsername();
        return UserCoreUtil.addTenantDomainToEntry(username, PrivilegedCarbonContext.getThreadLocalCarbonContext()
                .getTenantDomain());
    }

    private String getDecodedUserId(String encodedUserId) {

        User user = new UserIdToUser().apply(encodedUserId, ContextLoader.getTenantDomainFromContext());
        return user.toFullQualifiedUsername();
    }

    private URI getAssociationsLocationURI() {

        return buildURI(String.format(V1_API_PATH_COMPONENT + USER_ASSOCIATIONS_PATH_COMPONENT, ME_CONTEXT));
    }
}
