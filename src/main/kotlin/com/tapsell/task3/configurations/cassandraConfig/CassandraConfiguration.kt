package com.tapsell.task3.configurations.cassandraConfig

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration
import org.springframework.data.cassandra.config.CassandraClusterFactoryBean
import org.springframework.data.cassandra.config.SchemaAction
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories
import org.springframework.scheduling.annotation.EnableScheduling

const val KEY_SPACE_NAME = "advertiseStat"

@Configuration
@EnableScheduling // todo this config must move to another place
@EnableCassandraRepositories(basePackages = ["com.tapsell.task3.repositories"])
class CassandraConfiguration : AbstractCassandraConfiguration() {

    object TimeToLive {
        const val FOR_DAILY_STAT = 8 * 86400  // 8 days in millis
        const val FOR_WEEK_STAT = 2 * 86400  // 2 days in millis
    }

    object TableNames{
        const val ADVERTISE_EVENT = "adEvent"
        const val DAILY_STAT = "dailyStat"
        const val WEEK_STAT = "weekStat"
    }

    override fun getKeyspaceName(): String {
        return "advertiseStat"
    }

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