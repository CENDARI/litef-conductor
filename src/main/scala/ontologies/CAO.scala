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
    lazy val NS = "http://resources.cendari.dariah.eu/ontologies/cao#"

    /**
     * contact (property)
     *
     */
    lazy val `hasContact` = NS #> "hasContact"

    /**
     * access notes (property)
     *
     */
    lazy val `hasAccessNotes` = NS #> "hasAccessNotes"

    /**
     * holdings (property)
     *
     */
    lazy val `hasHoldingsInTopic` = NS #> "hasHoldingsInTopic"

    /**
     * types (property)
     *
     */
    lazy val `hasRepositoryTypes` = NS #> "hasRepositoryTypes"

    /**
     * audience (property)
     *
     */
    lazy val `hasAudience` = NS #> "hasAudience"

    /**
     * convention (property)
     *
     */
    lazy val `hasConventions` = NS #> "hasConventions"

    /**
     * Cendari Archival Ontology^^http://www.w3.org/2001/XMLSchema#string (class)
     * Ontology for mapping EAG files^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `metadata` = NS ## "metadata"

    /**
     * address type (property)
     * Type of address recorded as a repository address. Permits to specify what type of address is recorded (postal, visitors, etc.).
     *
     * Corresponds to /desc/repositories/repository/location@type attribute
     *
     */
    lazy val `addressType` = NS #> "addressType"

    /**
     * non preferred name (property)
     * see cao:InstitutionIdentity
     */
    lazy val `hasNonPreferredName` = NS #> "hasNonPreferredName"

    /**
     * Maintenance History (class)
     * Records maintenance information about the Institutional description such as who created the description, when it was modified, when it was published or deleted.
     *
     * Normally, should be taken as part of the repository internal information.
     *
     * Corresponds to /control/maintenanceHistory in Cendari@EAG and APEX EAG 2012
     */
    lazy val `MaintenanceHistory` = NS ## "MaintenanceHistory"

    /**
     * agency code (property)
     *
     */
    lazy val `hasAgencyCode` = NS #> "hasAgencyCode"

    /**
     * service (property)
     *
     */
    lazy val `hasService` = NS #> "hasService"

    /**
     * Conventions (class)
     * Conventions and Standards concept scheme.
     *
     * Used in cao:hasConventions to list all conventions and standards used within the EAG Document
     */
    lazy val `ConventionOrStandardScheme` = NS ## "ConventionOrStandardScheme"

    /**
     * Repository Type (class)
     * The type of repository. CAO models it as a skos:Concept, taken from the cao:CendariRepositoryTypesScheme concept scheme.
     *
     * Enumerated list is as follows (same in both Cendari@eag and EAG APEX 2012):
     *
     * -National archives
     * -Regional archives
     * -County/local authority archives
     * -Municipal archives
     * -Specialised governmental archives
     * -Private persons and family archives
     * -Church and religious archives
     * -Business archives
     * -University and research archives
     * -Media archives
     * -Archives of political parties, of popular/labour movement and other non-governmental organisations, associations, agencies and foundations
     * -Specialised non-governmental archives and archives of other cultural (heritage) institutions.
     *
     * Corresponds to the element archguide/identity/repositoryType, but is related as a property of the Repository and not as a property of the top level Institution described by EAG document.
     */
    lazy val `RepositoryType` = NS ## "RepositoryType"

    /**
     * Free (class)
     * Access and use of the repository material is free, and there are no restrictive conditions.
     */
    lazy val `FreeRepositoryAccess` = NS ## "FreeRepositoryAccess"

    /**
     * Script (property)
     * Scripts in Use
     * TODO: make separate class and generate vocabulary  from ISO 15924 List (create concept scheme for scripts in  ISO 15924 )
     */
    lazy val `hasScript` = NS #> "hasScript"

    /**
     * name (property)
     *
     */
    lazy val `hasAgencyName` = NS #> "hasAgencyName"

    /**
     *  (class)
     *
     */
    lazy val `Internal` = NS ## "Internal"

    /**
     * type (property)
     *
     */
    lazy val `isRepositoryOfType` = NS #> "isRepositoryOfType"

    /**
     * URI (property)
     * A URI to the resource from where repository history or holdings descriptions are created.
     *
     * Corresponds to /control/sources/source@href and /control/sources/source@id
     *
     *
     * In case there is not online representation of the resource, URI has to be provided as well and manually defined by the user.
     *
     * For example,  in this case it may be of a format such as :
     *
     * <scheme>:<some identifying string>
     *
     * where the scheme is practically a (cendari internal) URI scheme such as:
     *
     * caoid  (Cendari Archival Ontology Identifiers)
     *
     *
     *
     *
     *
     *
     */
    lazy val `URI` = NS #> "URI"

    /**
     *  (property)
     *
     */
    lazy val `repositoryId` = NS #> "repositoryId"

    /**
     * TermsOfUseURI (property)
     * Alternatively, external  (to be more accurate) URI to the document containing termsOfUse information.
     *
     * Can be used solely or as a complement to cao:termsOfUseNote
     */
    lazy val `termsOfUseURI` = NS #> "termsOfUseURI"

    /**
     * event (property)
     * property relating the maintenance history of the institution with the maintenance activity (prov:Activity)
     *
     */
    lazy val `hasMaintenanceEvent` = NS #> "hasMaintenanceEvent"

    /**
     * Maintenance Agency (class)
     * Maintenance agency responsible for the maintenance of the EAG Document.
     * In case of data maintained by Cendari, default agency is Cendari and agencyCode is EU-Cendari.
     *
     * Alternatively, APEX may be a maintenance agency.
     *
     * corresponds to /control/maintenanceAgency part of Cendari@EAG and APEX EAG 2012
     */
    lazy val `MaintenanceAgency` = NS ## "MaintenanceAgency"

    /**
     * Note (property)
     * Data property representing various notes associated to the institution and repository descriptions e.g. cao:holdingsNote, cao:repositoryHistoryNote, cao:sourceNote etc.
     */
    lazy val `Note` = NS #> "Note"

    /**
     * Address (class)
     * Repository address of a repository of an archival institution.
     * This is subclass of vcard:Address, but it additionally includes some more properties which are not in vcard:Address definition.
     */
    lazy val `RepositoryAddress` = NS ## "RepositoryAddress"

    /**
     * language (property)
     * Ranges to values from ISO-639-1 language vocabulaty.
     */
    lazy val `hasLanguage` = NS #> "hasLanguage"

    /**
     * Contributor (class)
     * Cendari contributor may be either a org:FormalOrganization or a foaf:Person.
     * In case of a foaf:Person, there is a posisbility to specify the affiliation (via cao:hasAffiliation org:FormalOrganization )
     */
    lazy val `Contributor` = NS ## "Contributor"

    /**
     * code (property)
     *
     */
    lazy val `hasCountryCode` = NS #> "hasCountryCode"

    /**
     * Repository (class)
     * A Repository of an archival institution
     * An institution may have more than one repositories which offer data services. An example for an institution is the NKP, and the repositories within are different archives represented  by Manuscriptorium.
     *
     *
     */
    lazy val `Repository` = NS ## "Repository"

    /**
     *  (property)
     *
     */
    lazy val `name` = NS #> "name"

    /**
     * address (property)
     *
     */
    lazy val `hasRepositoryAddress` = NS #> "hasRepositoryAddress"

    /**
     *  (property)
     *
     */
    lazy val `hasFax` = NS #> "hasFax"

    /**
     * repository (property)
     *
     */
    lazy val `hasRepository` = NS #> "hasRepository"

    /**
     * Convention (class)
     * Convention declaration. Represents a concept, a convention used in description of the institution.
     *
     * Corresponds to /control/conventionDeclaration
     *
     * in CAO it is used as a concept belonging to the Concept schema of conventions (see cao:ConventionOrStandardScheme )
     */
    lazy val `ConventionOrStandard` = NS ## "ConventionOrStandard"

    /**
     * Access (property)
     * Note about the conditions of Acess to the repository
     *
     * Can be added and provides detailed information about the “access policies, including any restrictions and/or regulations for the use of materials and facilities. Information about registration, appointments, readers’ tickets, letters of introduction, admission fees, etc. [can be recorded here]. Where appropriate, make reference to the relevant legislation.” (ISDIAH 5.4.2)
     */
    lazy val `restAccess` = NS #> "restAccess"

    /**
     * agency (property)
     *
     */
    lazy val `hasMaintenanceAgency` = NS #> "hasMaintenanceAgency"

    /**
     *  (property)
     *
     */
    lazy val `authForm` = NS #> "authForm"

    /**
     *  (class)
     *
     */
    lazy val `External` = NS ## "External"

    /**
     * Language Declaration (class)
     *
     * Contains information about the Languages and scripts used in the description of an archival institution.
     *
     * Corresponds to /control/languagedeclarations/languagedeclaration element of CENDARI@EAG profile.
     */
    lazy val `LanguageDeclaration` = NS ## "LanguageDeclaration"

    /**
     * Topics (class)
     * Cendari@EAG recommends only WW1 or Medieval.
     * By further development of the SKOS scheme however, topics can be further specialized.
     */
    lazy val `CendariTopicScheme` = NS ## "CendariTopicScheme"

    /**
     * Library (class)
     *
     */
    lazy val `LibraryService` = NS ## "LibraryService"

    /**
     * identity (property)
     * see cao:InstitutionIdentity
     */
    lazy val `hasIdentity` = NS #> "hasIdentity"

    /**
     * Contact (class)
     * Contact information for a repository service of an archival institution.
     *
     * Some services have  partial contact information such as email. phone. ocuppation. Some services additionally expose real persons behind. Thus a contact may have related a Person (with its own properties).
     * See also cao:isPerson property.
     *
     * Corresponds to /services/searchroom/contact element of CENDARI@EAG profile. Added to all services (not only search room)
     *
     */
    lazy val `Contact` = NS ## "Contact"

    /**
     * affiliation (property)
     *
     */
    lazy val `hasAffiliation` = NS #> "hasAffiliation"

    /**
     * Source (class)
     * Represents the sources from where archival institution description is provided.
     *
     * EAG and EAG@CENDARI assume that information about the archival institution may be created without source information.
     * This ontology makes it more restrictire, thus a source information must always exist.
     *
     * For a particular source, users may then create repository history notes (repositorhist) or holdings descriptions.
     */
    lazy val `SourceInformation` = NS ## "SourceInformation"

    /**
     * declaration (property)
     *
     */
    lazy val `hasLanguageDeclaration` = NS #> "hasLanguageDeclaration"

    /**
     * Audience (class)
     * This is a class which contains possiible values for the types of usage of the archival  description.
     * .
     * For archival institution descriptions, originally, allowed are "internal" or "external" as individuals, to point to the internal/external use of the Institutional description.
     * By default "external" should be used.
     *
     * Maps to eag@audience attribute of the top level eag element from Cendari@EAG profile
     */
    lazy val `Audience` = NS ## "Audience"

    /**
     * Search room (class)
     * Provides a description of the search room and facilities available.
     *
     *
     */
    lazy val `SearchRoomService` = NS ## "SearchRoomService"

    /**
     * parallel name (property)
     * see cao:InstitutionIdentity
     */
    lazy val `hasParallelName` = NS #> "hasParallelName"

    /**
     * Holdings (property)
     * Based on specified source (with URL, SourceNote)
     * corresponding to /holdings/descriptiveNote element in CENDARI@EAG
     */
    lazy val `holdingsNote` = NS #> "holdingsNote"

    /**
     * Institution (class)
     * Archive, Library, Museum etc. described with EAG
     *
     */
    lazy val `Institution` = NS ## "Institution"

    /**
     *  (property)
     *
     */
    lazy val `parForm` = NS #> "parForm"

    /**
     * Technical (class)
     *
     */
    lazy val `TechnicalService` = NS ## "TechnicalService"

    /**
     * conventions (property)
     *
     */
    lazy val `usesConventionsOrStandards` = NS #> "usesConventionsOrStandards"

    /**
     * history (property)
     *
     */
    lazy val `hasMaintenanceHistory` = NS #> "hasMaintenanceHistory"

    /**
     * Country code (class)
     * Individuals of this class are country codes corresponding to ISO 3166-1-alpha-2 code.
     *
     */
    lazy val `ISO3166-1` = NS ## "ISO3166-1"

    /**
     * sources (property)
     *
     */
    lazy val `hasSources` = NS #> "hasSources"

    /**
     *  (property)
     *
     */
    lazy val `hasPhone` = NS #> "hasPhone"

    /**
     * Latest (property)
     * Can be defined as an latest  "known" date of the content contained in some information.
     *
     * Initial use is for cao:InstitutionIdentity, describing latest date when the institution is represented with this identity information.
     */
    lazy val `notAfter` = NS #> "notAfter"

    /**
     *  (property)
     *
     */
    lazy val `occupation` = NS #> "occupation"

    /**
     * Source (property)
     * Descriptive information about the source from where descriptions of the repository history or holdings are coming e.g. "the web site of the archive" (free text)
     *
     * Corresponds to /control/sources/sourceEntry element
     */
    lazy val `sourceNote` = NS #> "sourceNote"

    /**
     * Earliest (property)
     * Can be defined as an earliest "known" date of the content contained in some information.
     *
     * Initial use is for cao:InstitutionIdentity, describing earliest date when the institution is known represented with this identity information.
     */
    lazy val `notBefore` = NS #> "notBefore"

    /**
     * History (property)
     * corresponds to /desc/repositorhist/descriptiveNote of CENDARI@EAG
     *
     * A note on repository history, based on referenced source
     */
    lazy val `repositoryHistoryNote` = NS #> "repositoryHistoryNote"

    /**
     * Identity (class)
     * Identyfing information about an institution holds properties such as identifier, country code, former names of the institution etc..
     *
     * Corresponds to archgude/identity element of the CENDARI@EAG profile.
     *
     *
     */
    lazy val `InstitutionIdentity` = NS ## "InstitutionIdentity"

    /**
     * Recreation (class)
     *
     */
    lazy val `RecreationalService` = NS ## "RecreationalService"

    /**
     * Conditional access (class)
     * There are restrictive conditions for use and access to the repository material.
     */
    lazy val `ConditionalRepositoryAccess` = NS ## "ConditionalRepositoryAccess"

    /**
     * termsOfUse (property)
     * Incorporates embodied textual information (and links) to the terms of use, archival law or fee structure.
     *
     * Can be used as complement or solely to the cao:termsOfUseURI
     */
    lazy val `termsOfUseNote` = NS #> "termsOfUseNote"

    /**
     * topics (property)
     *
     */
    lazy val `hasTopics` = NS #> "hasTopics"

    /**
     * Status (property)
     * corresponds to /eag/control/maintenancestatus
     *
     * Maintenance status of the institutional description. Possible values:
     * new, cancelled, deleted, deletedReplaced, deletedMerged, deletedSplit, derived or revised
     */
    lazy val `hasMaintenanceStatus` = NS #> "hasMaintenanceStatus"

    /**
     * Repository Services (class)
     * Types of services a repository offers. There can be different types of services, each with own contact information. They are modelled as subclasses of cao:RepositoryServices.
     *
     * Corresponds to /repository/services/<servicetype> elements of APEX EAG 2012
     *
     *
     *
     */
    lazy val `RepositoryServices` = NS ## "RepositoryServices"

    /**
     * Internet Access (class)
     *
     */
    lazy val `InternetAccessService` = NS ## "InternetAccessService"

    /**
     * Access Type (class)
     * Type of access to the material/data in the repository.
     *
     * EAG@CENDARI thus CAO allows for two types (modelled as subclasses) of repository access:
     *
     * ConditionalRepositoryAccess
     * FreeRepositoryAccess
     *
     * Its properties allow to additionally describe the terms of use or  potentially provide URI to the document containing the terms of use.
     *
     * hasAccess notes property of the Repository defines which kind of repository access a repository offers.
     *
     * This class practically corresponds to the element /repository/access of the Cendari@EAG and APEX EAG 2012
     */
    lazy val `RepositoryAccess` = NS ## "RepositoryAccess"

    /**
     * isPerson (property)
     *
     */
    lazy val `isPerson` = NS #> "isPerson"

    /**
     * Topic (class)
     * Topics covered by the Institution. It is a concept organized within  cao:CendariTopicScheme.
     *
     * Cendari@EAG recommends only WW1 or Medieval.
     * By further development of the SKOS scheme however, topics can be further specialized.
     */
    lazy val `Topic` = NS ## "Topic"

    /**
     * agent (property)
     *
     */
    lazy val `hasAgent` = NS #> "hasAgent"

    /**
     * Repository Types (class)
     * Initial concepts here are same in both Cendari@eag and EAG APEX 2012:
     *
     * -National archives
     * -Regional archives
     * -County/local authority archives
     * -Municipal archives
     * -Specialised governmental archives
     * -Private persons and family archives
     * -Church and religious archives
     * -Business archives
     * -University and research archives
     * -Media archives
     * -Archives of political parties, of popular/labour movement and other non-governmental organisations, associations, agencies and foundations
     * -Specialised non-governmental archives and archives of other cultural (heritage) institutions.
     *
     *
     * Corresponds to the element archguide/identity/repositoryType.
     * CAO relates it as a property of the Repository and not as a property of the top level Institution described by EAG document.
     *
     *
     */
    lazy val `CendariRepositoryTypeScheme` = NS ## "CendariRepositoryTypeScheme"

    /**
     * Delete (class)
     * Type of activity which expresses "soft" deletion of an entity to which it is associated. In CENDARI case, it is part of the institution maintenance history, thus expresses deletion of the institutional description.
     *
     * In real terms, this would mean the institutional description record is withdrawn from usage and not physically deleted.
     */
    lazy val `Delete` = NS ## "Delete"

    /**
     * id (property)
     *
     */
    lazy val `hasRecordId` = NS #> "hasRecordId"


}
