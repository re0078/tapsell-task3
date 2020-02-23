package com.tapsell.task2.configs

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration
import org.springframework.data.cassandra.config.CassandraClusterFactoryBean
import org.springframework.data.cassandra.config.SchemaAction
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories


@Configuration
@EnableCassandraRepositories(basePackages = ["com.tapsell.task3.repositories"])
class CassandraConfiguration : AbstractCassandraConfiguration() {


    override fun getKeyspaceName(): String {
        return "rE"
    } // todo to be created

    override fun getContactPoints(): String {
        return "172.30.30.138"
    }

    @Bean
    override fun cluster(): CassandraClusterFactoryBean {
        val cluster = CassandraClusterFactoryBean()
        cluster.setContactPoints("172.30.30.138")
        cluster.setPort(9042)
        return cluster
    }


    override fun getSchemaAction(): SchemaAction {
        return SchemaAction.CREATE_IF_NOT_EXISTS
    }

}