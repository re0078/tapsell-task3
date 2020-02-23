package com.tapsell.task3.configurations

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration
import org.springframework.data.cassandra.config.CassandraClusterFactoryBean
import org.springframework.data.cassandra.config.SchemaAction
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled


@Configuration
@EnableScheduling // todo this config must move to another place
@EnableCassandraRepositories(basePackages = ["com.tapsell.task3.repositories"])
class CassandraConfiguration : AbstractCassandraConfiguration() {


    override fun getKeyspaceName(): String {
        return "advertiseStat"
    } // todo to be created

    override fun getContactPoints(): String {
        return "localhost"
    }

    @Bean
    override fun cluster(): CassandraClusterFactoryBean {
        val cluster = CassandraClusterFactoryBean()
        cluster.setContactPoints("localhost")
        cluster.setPort(9042)
        return cluster
    }


    override fun getSchemaAction(): SchemaAction {
        return SchemaAction.CREATE_IF_NOT_EXISTS
    }

}