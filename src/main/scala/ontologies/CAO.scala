/*
 * This file has been automatically generated based on the
 * CAO ontology.
 */

package cendari.ontology

import javelin.ontology.Implicits._

 /**
  * Cendari Archival Ontology
  * Ontology for mapping EAG files
  */
object CAO {
    lazy val NS = "http://cendari.eu/ontologies/cendari/eag/"

    /**
     *  (property)
     *
     */
    lazy val `sourceNote` = NS #> "sourceNote"

    /**
     *  (property)
     *
     */
    lazy val `hasConventions` = NS #> "hasConventions"

    /**
     *  (class)
     *
     */
    lazy val `FreeRepositoryAccess` = NS ## "FreeRepositoryAccess"

    /**
     *  (property)
     *
     */
    lazy val `authForm` = NS #> "authForm"

    /**
     *  (property)
     *
     */
    lazy val `Note` = NS #> "Note"

    /**
     *  (class)
     *
     */
    lazy val `Person` = NS ## "Person"

    /**
     *  (property)
     *
     */
    lazy val `isRepositoryOfType` = NS #> "isRepositoryOfType"

    /**
     *  (class)
     *
     */
    lazy val `CendariRepositoryTypeScheme` = NS ## "CendariRepositoryTypeScheme"

    /**
     *  (property)
     *
     */
    lazy val `restAccess` = NS #> "restAccess"

    /**
     *  (property)
     *
     */
    lazy val `hasTopics` = NS #> "hasTopics"

    /**
     *  (property)
     *
     */
    lazy val `occupation` = NS #> "occupation"

    /**
     *  (property)
     *
     */
    lazy val `hasMaintenanceAgency` = NS #> "hasMaintenanceAgency"

    /**
     *  (class)
     *
     */
    lazy val `RepositoryAccess` = NS ## "RepositoryAccess"

    /**
     *  (property)
     *
     */
    lazy val `hasAudience` = NS #> "hasAudience"

    /**
     *  (property)
     *
     */
    lazy val `hasMaintenanceStatus` = NS #> "hasMaintenanceStatus"

    /**
     *  (property)
     *
     */
    lazy val `hasPhone` = NS #> "hasPhone"

    /**
     *  (property)
     *
     */
    lazy val `hasRepository` = NS #> "hasRepository"

    /**
     *  (property)
     *
     */
    lazy val `hasAgent` = NS #> "hasAgent"

    /**
     *  (property)
     *
     */
    lazy val `URI` = NS #> "URI"

    /**
     *  (property)
     *
     */
    lazy val `hasRepositoryAddress` = NS #> "hasRepositoryAddress"

    /**
     *  (class)
     *
     */
    lazy val `MaintenanceHistory` = NS ## "MaintenanceHistory"

    /**
     *  (property)
     *
     */
    lazy val `addressType` = NS #> "addressType"

    /**
     *  (property)
     *
     */
    lazy val `hasParallelName` = NS #> "hasParallelName"

    /**
     *  (property)
     *
     */
    lazy val `name` = NS #> "name"

    /**
     *  (property)
     *
     */
    lazy val `parForm` = NS #> "parForm"

    /**
     *  (class)
     *
     */
    lazy val `RepositoryAddress` = NS ## "RepositoryAddress"

    /**
     *  (class)
     *
     */
    lazy val `CendariTopicScheme` = NS ## "CendariTopicScheme"

    /**
     *  (property)
     *
     */
    lazy val `hasFax` = NS #> "hasFax"

    /**
     *  (class)
     *
     */
    lazy val `ConventionOrStandardScheme` = NS ## "ConventionOrStandardScheme"

    /**
     *  (property)
     *
     */
    lazy val `repositoryId` = NS #> "repositoryId"

    /**
     *  (class)
     *
     */
    lazy val `MaintenanceAgency` = NS ## "MaintenanceAgency"

    /**
     *  (class)
     *
     */
    lazy val `Topic` = NS ## "Topic"

    /**
     *  (class)
     *
     */
    lazy val `ISO3166-1` = NS ## "ISO3166-1"

    /**
     *  (property)
     * TODO: make it an object property from ISO 15924 List (create concept scheme for scripts in  ISO 15924 )
     */
    lazy val `hasScript` = NS #> "hasScript"

    /**
     *  (class)
     * TODO: RepositoryHoldings and descritions as a Type with sources
     * Also check if necessary to add sources at top level of EAG
     * Archive, Library, Museum etc. described with EAG
     */
    lazy val `Institution` = NS ## "Institution"

    /**
     *  (property)
     *
     */
    lazy val `givenName` = NS #> "givenName"

    /**
     *  (class)
     *
     */
    lazy val `Repository` = NS ## "Repository"

    /**
     *  (class)
     *
     */
    lazy val `MaintenanceEvent` = NS ## "MaintenanceEvent"

    /**
     *  (class)
     *
     */
    lazy val `ConventionOrStandard` = NS ## "ConventionOrStandard"

    /**
     *  (property)
     *
     */
    lazy val `hasLanguage` = NS #> "hasLanguage"

    /**
     *  (class)
     *
     */
    lazy val `InstitutionIdentity` = NS ## "InstitutionIdentity"

    /**
     *  (class)
     *
     */
    lazy val `Contributor` = NS ## "Contributor"

    /**
     *  (class)
     *
     */
    lazy val `SearchRoomService` = NS ## "SearchRoomService"

    /**
     *  (property)
     *
     */
    lazy val `hasRecordId` = NS #> "hasRecordId"

    /**
     *  (class)
     *
     */
    lazy val `RepositoryType` = NS ## "RepositoryType"

    /**
     *  (property)
     *
     */
    lazy val `usesConventionsOrStandards` = NS #> "usesConventionsOrStandards"

    /**
     *  (property)
     *
     */
    lazy val `hasHoldingsInTopic` = NS #> "hasHoldingsInTopic"

    /**
     *  (property)
     *
     */
    lazy val `hasAffiliation` = NS #> "hasAffiliation"

    /**
     *  (property)
     *
     */
    lazy val `hasAudiences` = NS #> "hasAudiences"

    /**
     *  (property)
     *
     */
    lazy val `hasAccessNotes` = NS #> "hasAccessNotes"

    /**
     *  (class)
     *
     */
    lazy val `HoldingsInformation` = NS ## "HoldingsInformation"

    /**
     * Cendari Archival Ontology^^http://www.w3.org/2001/XMLSchema#string (class)
     * Ontology for mapping EAG files^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `metadata` = NS ## "metadata"

    /**
     *  (property)
     *
     */
    lazy val `hasIdentity` = NS #> "hasIdentity"

    /**
     *  (property)
     *
     */
    lazy val `hasNonPreferredName` = NS #> "hasNonPreferredName"

    /**
     *  (property)
     *
     */
    lazy val `hasRepositoryTypes` = NS #> "hasRepositoryTypes"

    /**
     *  (class)
     *
     */
    lazy val `ConditionalRepositoryAccess` = NS ## "ConditionalRepositoryAccess"

    /**
     *  (class)
     *
     */
    lazy val `MaintenanceStatus` = NS ## "MaintenanceStatus"

    /**
     *  (property)
     *
     */
    lazy val `holdingsNote` = NS #> "holdingsNote"

    /**
     *  (property)
     *
     */
    lazy val `termsOfUseURI` = NS #> "termsOfUseURI"

    /**
     *  (property)
     *
     */
    lazy val `termsOfUseNote` = NS #> "termsOfUseNote"

    /**
     *  (property)
     *
     */
    lazy val `hasMaintenanceHistory` = NS #> "hasMaintenanceHistory"

    /**
     *  (property)
     *
     */
    lazy val `hasSources` = NS #> "hasSources"

    /**
     *  (property)
     *
     */
    lazy val `hasAgencyName` = NS #> "hasAgencyName"

    /**
     *  (class)
     *
     */
    lazy val `cndr_u6qoRw1R` = NS ## "cndr_u6qoRw1R"

    /**
     *  (class)
     *
     */
    lazy val `LanguageDeclaration` = NS ## "LanguageDeclaration"

    /**
     *  (class)
     *
     */
    lazy val `TechnicalService` = NS ## "TechnicalService"

    /**
     *  (class)
     *
     */
    lazy val `RepositoryServices` = NS ## "RepositoryServices"

    /**
     *  (property)
     *
     */
    lazy val `notBefore` = NS #> "notBefore"

    /**
     *  (property)
     *
     */
    lazy val `hasCountryCode` = NS #> "hasCountryCode"

    /**
     *  (property)
     *
     */
    lazy val `isPerson` = NS #> "isPerson"

    /**
     *  (class)
     *
     */
    lazy val `RecreationalService` = NS ## "RecreationalService"

    /**
     *  (class)
     *
     */
    lazy val `SourceInformation` = NS ## "SourceInformation"

    /**
     *  (class)
     *
     */
    lazy val `LibraryService` = NS ## "LibraryService"

    /**
     *  (property)
     *
     */
    lazy val `hasLanguageDeclaration` = NS #> "hasLanguageDeclaration"

    /**
     *  (property)
     *
     */
    lazy val `hasContact` = NS #> "hasContact"

    /**
     *  (property)
     *
     */
    lazy val `notAfter` = NS #> "notAfter"

    /**
     *  (property)
     *
     */
    lazy val `repositoryHistoryNote` = NS #> "repositoryHistoryNote"

    /**
     *  (property)
     *
     */
    lazy val `hasMaintenanceEvent` = NS #> "hasMaintenanceEvent"

    /**
     *  (class)
     *
     */
    lazy val `Delete` = NS ## "Delete"

    /**
     *  (class)
     *
     */
    lazy val `Contact` = NS ## "Contact"

    /**
     *  (property)
     *
     */
    lazy val `familyName` = NS #> "familyName"

    /**
     *  (property)
     *
     */
    lazy val `hasService` = NS #> "hasService"

    /**
     *  (class)
     *
     */
    lazy val `Audience` = NS ## "Audience"

    /**
     *  (class)
     *
     */
    lazy val `AudienceScheme` = NS ## "AudienceScheme"

    /**
     *  (class)
     *
     */
    lazy val `InternetAccessService` = NS ## "InternetAccessService"

    /**
     *  (property)
     *
     */
    lazy val `hasAgencyCode` = NS #> "hasAgencyCode"


}
