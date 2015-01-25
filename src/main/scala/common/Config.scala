/*
 * Copyright (C) 2014 Ivan Cukic <ivan at mi.sanu.ac.rs>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package common

import com.typesafe.config.ConfigFactory

/**
 * Typed configuration class
 */
object Config {
    lazy val config = ConfigFactory.load getConfig "litef"

    lazy val namespace = config getString "namespace"

    object Ckan {
        lazy val home          = config getString "ckan.home"
        lazy val namespace     = config getString "ckan.namespace"
        lazy val httpUsername  = config getString "ckan.httpUsername"
        lazy val httpPassword  = config getString "ckan.httpPassword"
        lazy val apiKey        = config getString "ckan.apiKey"

        lazy val localStoragePrefix = config getString "ckan.localStoragePrefix"

        object Database {
            lazy val url       = config getString "ckan.postgres.url"
            lazy val user      = config getString "ckan.postgres.username"
            lazy val password  = config getString "ckan.postgres.password"
            lazy val driver    = config getString "ckan.postgres.driver"
        }
    }

    object Virtuoso {
        lazy val url           = config getString "virtuoso.url"
        lazy val user          = config getString "virtuoso.username"
        lazy val password      = config getString "virtuoso.password"
    }

    object Nerd {
        lazy val namespace     = config getString "nerd.namespace"
    }

    object Conductor {
        lazy val fileSizeLimit = config getLong "conductor.fileSizeLimit"
        lazy val plugins       = config getString "conductor.plugins" split ','
    }

    object Indexer {
        lazy val localStoragePrefix = config getString "indexer.localStoragePrefix"
    }
}
