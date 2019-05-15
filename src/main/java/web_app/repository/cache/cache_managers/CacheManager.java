package web_app.repository.cache.cache_managers;

import web_app.repository.cache.cache_connectors.CacheConnector;

public interface CacheManager {
    CacheConnector getCacheConnector() throws Exception;
}
