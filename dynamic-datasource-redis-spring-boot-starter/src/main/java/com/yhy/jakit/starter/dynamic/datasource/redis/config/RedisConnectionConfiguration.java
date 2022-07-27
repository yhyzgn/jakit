package com.yhy.jakit.starter.dynamic.datasource.redis.config;

import com.yhy.jakit.starter.dynamic.datasource.redis.exception.RedisUrlSyntaxException;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.connection.*;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2021-05-25 14:57
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class RedisConnectionConfiguration {

    protected final RedisStandaloneConfiguration getStandaloneConfig(RedisProperties properties) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        if (StringUtils.hasText(properties.getUrl())) {
            ConnectionInfo connectionInfo = parseUrl(properties.getUrl());
            config.setHostName(connectionInfo.getHostName());
            config.setPort(connectionInfo.getPort());
            config.setUsername(connectionInfo.getUsername());
            config.setPassword(RedisPassword.of(connectionInfo.getPassword()));
        } else {
            config.setHostName(properties.getHost());
            config.setPort(properties.getPort());
            config.setUsername(properties.getUsername());
            config.setPassword(RedisPassword.of(properties.getPassword()));
        }
        config.setDatabase(properties.getDatabase());
        return config;
    }

    protected final RedisSentinelConfiguration getSentinelConfig(RedisProperties properties) {
        RedisProperties.Sentinel sentinelProperties = properties.getSentinel();
        if (sentinelProperties != null) {
            RedisSentinelConfiguration config = new RedisSentinelConfiguration();
            config.master(sentinelProperties.getMaster());
            config.setSentinels(createSentinels(sentinelProperties));
            config.setUsername(properties.getUsername());
            if (properties.getPassword() != null) {
                config.setPassword(RedisPassword.of(properties.getPassword()));
            }
            if (sentinelProperties.getPassword() != null) {
                config.setSentinelPassword(RedisPassword.of(sentinelProperties.getPassword()));
            }
            config.setDatabase(properties.getDatabase());
            return config;
        }
        return null;
    }

    /**
     * Create a {@link RedisClusterConfiguration} if necessary.
     *
     * @return {@literal null} if no cluster settings are set.
     */
    protected final RedisClusterConfiguration getClusterConfiguration(RedisProperties properties) {
        if (properties.getCluster() == null) {
            return null;
        }
        RedisProperties.Cluster clusterProperties = properties.getCluster();
        RedisClusterConfiguration config = new RedisClusterConfiguration(clusterProperties.getNodes());
        if (clusterProperties.getMaxRedirects() != null) {
            config.setMaxRedirects(clusterProperties.getMaxRedirects());
        }
        config.setUsername(properties.getUsername());
        if (properties.getPassword() != null) {
            config.setPassword(RedisPassword.of(properties.getPassword()));
        }
        return config;
    }

    private List<RedisNode> createSentinels(RedisProperties.Sentinel sentinel) {
        List<RedisNode> nodes = new ArrayList<>();
        for (String node : sentinel.getNodes()) {
            try {
                String[] parts = StringUtils.split(node, ":");
                Assert.state(parts.length == 2, "Must be defined as 'host:port'");
                nodes.add(new RedisNode(parts[0], Integer.parseInt(parts[1])));
            } catch (RuntimeException ex) {
                throw new IllegalStateException("Invalid redis sentinel property '" + node + "'", ex);
            }
        }
        return nodes;
    }

    protected ConnectionInfo parseUrl(String url) {
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            if (!"redis".equals(scheme) && !"rediss".equals(scheme)) {
                throw new RedisUrlSyntaxException(url);
            }
            boolean useSsl = ("rediss".equals(scheme));
            String username = null;
            String password = null;
            if (uri.getUserInfo() != null) {
                String candidate = uri.getUserInfo();
                int index = candidate.indexOf(':');
                if (index >= 0) {
                    username = candidate.substring(0, index);
                    password = candidate.substring(index + 1);
                } else {
                    password = candidate;
                }
            }
            return new ConnectionInfo(uri, useSsl, username, password);
        } catch (URISyntaxException ex) {
            throw new RedisUrlSyntaxException(url, ex);
        }
    }

    static class ConnectionInfo {

        private final URI uri;

        private final boolean useSsl;

        private final String username;

        private final String password;

        ConnectionInfo(URI uri, boolean useSsl, String username, String password) {
            this.uri = uri;
            this.useSsl = useSsl;
            this.username = username;
            this.password = password;
        }

        boolean isUseSsl() {
            return this.useSsl;
        }

        String getHostName() {
            return this.uri.getHost();
        }

        int getPort() {
            return this.uri.getPort();
        }

        String getUsername() {
            return this.username;
        }

        String getPassword() {
            return this.password;
        }

    }
}
