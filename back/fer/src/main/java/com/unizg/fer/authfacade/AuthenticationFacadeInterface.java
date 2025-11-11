package com.unizg.fer.authfacade;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author Martin NERON
 * Interface that allow to retrieve the current user in the session anywhere
 */
public interface AuthenticationFacadeInterface {
    JsonNode getAuthentication();
}
