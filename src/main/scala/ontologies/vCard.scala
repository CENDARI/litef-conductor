/*
 * This file has been automatically generated based on the
 * vCard ontology.
 */

package w3c.ontology

import javelin.ontology.Implicits._

 /**
  * W3C Ontology for vCard
  * Ontology for vCard based on RFC6350
  */
object vCard {
    lazy val NS = "http://www.w3.org/2006/vcard/ns#"

    /**
     * Cell^^http://www.w3.org/2001/XMLSchema#string (class)
     * Also called mobile telephone^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `Cell` = NS ## "Cell"

    /**
     * Kin^^http://www.w3.org/2001/XMLSchema#string (class)
     * 
     */
    lazy val `Kin` = NS ## "Kin"

    /**
     * Pref@en (class)
     * This class is deprecated
     */
    lazy val `Pref` = NS ## "Pref"

    /**
     * Female^^http://www.w3.org/2001/XMLSchema#string (class)
     * 
     */
    lazy val `Female` = NS ## "Female"

    /**
     * has country name^^http://www.w3.org/2001/XMLSchema#string (property)
     * Used to support property parameters for the country name data property^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `hasCountryName` = NS #> "hasCountryName"

    /**
     * has related^^http://www.w3.org/2001/XMLSchema#string (property)
     * To specify a relationship between another entity and the entity represented by this object^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `hasRelated` = NS #> "hasRelated"

    /**
     * Relation Type^^http://www.w3.org/2001/XMLSchema#string (class)
     * Used for relation type codes. The URI of the relation type code must be used as the value for the Relation Type.
     */
    lazy val `RelatedType` = NS ## "RelatedType"

    /**
     * Organization^^http://www.w3.org/2001/XMLSchema#string (class)
     * An object representing an organization.  An organization is a single entity, and might represent a business or government, a department or division within a business or government, a club, an association, or the like.
     * ^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `Organization` = NS ## "Organization"

    /**
     * sound@en (property)
     * This object property has been deprecated^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `sound` = NS #> "sound"

    /**
     * Sibling^^http://www.w3.org/2001/XMLSchema#string (class)
     * 
     */
    lazy val `Sibling` = NS ## "Sibling"

    /**
     * Coresident^^http://www.w3.org/2001/XMLSchema#string (class)
     * 
     */
    lazy val `Coresident` = NS ## "Coresident"

    /**
     * role^^http://www.w3.org/2001/XMLSchema#string (property)
     * To specify the function or part played in a particular situation by the object
     */
    lazy val `role` = NS #> "role"

    /**
     * category^^http://www.w3.org/2001/XMLSchema#string (property)
     * The category information about the object, also known as tags
     */
    lazy val `category` = NS #> "category"

    /**
     * given name^^http://www.w3.org/2001/XMLSchema#string (property)
     * The given name associated with the object
     */
    lazy val `given-name` = NS #> "given-name"

    /**
     * Coworker^^http://www.w3.org/2001/XMLSchema#string (class)
     * 
     */
    lazy val `Coworker` = NS ## "Coworker"

    /**
     * Parcel@en (class)
     * This class is deprecated
     */
    lazy val `Parcel` = NS ## "Parcel"

    /**
     * has sound^^http://www.w3.org/2001/XMLSchema#string (property)
     * To specify a digital sound content information that annotates some aspect of the object^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `hasSound` = NS #> "hasSound"

    /**
     * Friend^^http://www.w3.org/2001/XMLSchema#string (class)
     * 
     */
    lazy val `Friend` = NS ## "Friend"

    /**
     * has honorific suffix@en (property)
     * Used to support property parameters for the honorific suffix data property^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `hasHonorificSuffix` = NS #> "hasHonorificSuffix"

    /**
     * has note@en (property)
     * Used to support property parameters for the note data property^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `hasNote` = NS #> "hasNote"

    /**
     * has postal code@en (property)
     * Used to support property parameters for the postal code data property^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `hasPostalCode` = NS #> "hasPostalCode"

    /**
     * Label@en (class)
     * This class is deprecated
     */
    lazy val `Label` = NS ## "Label"

    /**
     * street address^^http://www.w3.org/2001/XMLSchema#string (property)
     * The street address associated with the address of the object
     */
    lazy val `street-address` = NS #> "street-address"

    /**
     * name@en (property)
     * This object property has been deprecated
     */
    lazy val `n` = NS #> "n"

    /**
     * Type^^http://www.w3.org/2001/XMLSchema#string (class)
     * Used for type codes. The URI of the type code must be used as the value for Type.
     */
    lazy val `Type` = NS ## "Type"

    /**
     * Parent^^http://www.w3.org/2001/XMLSchema#string (class)
     * 
     */
    lazy val `Parent` = NS ## "Parent"

    /**
     * Text phone^^http://www.w3.org/2001/XMLSchema#string (class)
     * 
     */
    lazy val `TextPhone` = NS ## "TextPhone"

    /**
     * has messaging^^http://www.w3.org/2001/XMLSchema#string (property)
     * To specify the instant messaging and presence protocol communications with the object. (Was called IMPP in RFC6350)^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `hasInstantMessage` = NS #> "hasInstantMessage"

    /**
     * has role@en (property)
     * Used to support property parameters for the role data property^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `hasRole` = NS #> "hasRole"

    /**
     * Acquaintance^^http://www.w3.org/2001/XMLSchema#string (class)
     * 
     */
    lazy val `Acquaintance` = NS ## "Acquaintance"

    /**
     * Pager^^http://www.w3.org/2001/XMLSchema#string (class)
     * 
     */
    lazy val `Pager` = NS ## "Pager"

    /**
     * has url^^http://www.w3.org/2001/XMLSchema#string (property)
     * To specify a uniform resource locator associated with the object^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `hasURL` = NS #> "hasURL"

    /**
     * Home^^http://www.w3.org/2001/XMLSchema#string (class)
     * This implies that the property is related to an individual's personal life^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `Home` = NS ## "Home"

    /**
     * has given name@en (property)
     * Used to support property parameters for the given name data property^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `hasGivenName` = NS #> "hasGivenName"

    /**
     * latitude@en (property)
     * This data property has been deprecated
     */
    lazy val `latitude` = NS #> "latitude"

    /**
     * Email^^http://www.w3.org/2001/XMLSchema#string (class)
     * To specify the electronic mail address for communication with the object the vCard represents. Use the hasEmail object property.^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `Email` = NS ## "Email"

    /**
     * Dom@en (class)
     * This class is deprecated
     */
    lazy val `Dom` = NS ## "Dom"

    /**
     * has logo^^http://www.w3.org/2001/XMLSchema#string (property)
     * To specify a graphic image of a logo associated with the object ^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `hasLogo` = NS #> "hasLogo"

    /**
     * organizational unit name^^http://www.w3.org/2001/XMLSchema#string (property)
     * To specify the organizational unit name associated with the object
     */
    lazy val `organization-unit` = NS #> "organization-unit"

    /**
     * Tel@en (class)
     * This class is deprecated. Use the hasTelephone object property.
     */
    lazy val `Tel` = NS ## "Tel"

    /**
     * has title@en (property)
     * Used to support property parameters for the title data property^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `hasTitle` = NS #> "hasTitle"

    /**
     * has geo^^http://www.w3.org/2001/XMLSchema#string (property)
     * To specify information related to the global positioning of the object. May also be used as a property parameter.^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `hasGeo` = NS #> "hasGeo"

    /**
     * Colleague^^http://www.w3.org/2001/XMLSchema#string (class)
     * 
     */
    lazy val `Colleague` = NS ## "Colleague"

    /**
     * VCard@en (class)
     * The vCard class is deprecated and equivalent to the new Kind class, which is the parent for the four explicit types of vCards (Individual, Organization, Location, Group)
     */
    lazy val `VCard` = NS ## "VCard"

    /**
     * nickname^^http://www.w3.org/2001/XMLSchema#string (property)
     * The nick name associated with the object
     */
    lazy val `nickname` = NS #> "nickname"

    /**
     * Video^^http://www.w3.org/2001/XMLSchema#string (class)
     * 
     */
    lazy val `Video` = NS ## "Video"

    /**
     * Voice^^http://www.w3.org/2001/XMLSchema#string (class)
     * 
     */
    lazy val `Voice` = NS ## "Voice"

    /**
     * label@en (property)
     * This data property has been deprecated
     */
    lazy val `label` = NS #> "label"

    /**
     * Met^^http://www.w3.org/2001/XMLSchema#string (class)
     * 
     */
    lazy val `Met` = NS ## "Met"

    /**
     * has name^^http://www.w3.org/2001/XMLSchema#string (property)
     * To specify the components of the name of the object^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `hasName` = NS #> "hasName"

    /**
     * has street address@en (property)
     * Used to support property parameters for the street address data property^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `hasStreetAddress` = NS #> "hasStreetAddress"

    /**
     * has address^^http://www.w3.org/2001/XMLSchema#string (property)
     * To specify the components of the delivery address for the object^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `hasAddress` = NS #> "hasAddress"

    /**
     * X400@en (class)
     * This class is deprecated
     */
    lazy val `X400` = NS ## "X400"

    /**
     * Unknown^^http://www.w3.org/2001/XMLSchema#string (class)
     * 
     */
    lazy val `Unknown` = NS ## "Unknown"

    /**
     * agent@en (property)
     * This object property has been deprecated
     */
    lazy val `agent` = NS #> "agent"

    /**
     * Address^^http://www.w3.org/2001/XMLSchema#string (class)
     * To specify the components of the delivery address for the  object^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `Address` = NS ## "Address"

    /**
     * Msg@en (class)
     * This class is deprecated
     */
    lazy val `Msg` = NS ## "Msg"

    /**
     * geo^^http://www.w3.org/2001/XMLSchema#string (property)
     * This object property has been deprecated^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `geo` = NS #> "geo"

    /**
     * has gender@en (property)
     * To specify  the sex or gender identity of the object.
     * URIs are recommended to enable interoperable sex and gender codes to be used.^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `hasGender` = NS #> "hasGender"

    /**
     * Location^^http://www.w3.org/2001/XMLSchema#string (class)
     * An object representing a named geographical place^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `Location` = NS ## "Location"

    /**
     * postal code^^http://www.w3.org/2001/XMLSchema#string (property)
     * The postal code associated with the address of the object
     */
    lazy val `postal-code` = NS #> "postal-code"

    /**
     * longitude@en (property)
     * This data property has been deprecated
     */
    lazy val `longitude` = NS #> "longitude"

    /**
     * has calendar busy^^http://www.w3.org/2001/XMLSchema#string (property)
     * To specify the busy time associated with the object. (Was called FBURL in RFC6350)^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `hasCalendarBusy` = NS #> "hasCalendarBusy"

    /**
     * has uid@en (property)
     * To specify a value that represents a globally unique identifier corresponding to the object^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `hasUID` = NS #> "hasUID"

    /**
     * Sweetheart^^http://www.w3.org/2001/XMLSchema#string (class)
     * 
     */
    lazy val `Sweetheart` = NS ## "Sweetheart"

    /**
     * sort as^^http://www.w3.org/2001/XMLSchema#string (property)
     * To specify the string to be used for national-language-specific sorting. Used as a property parameter only.
     */
    lazy val `sort-string` = NS #> "sort-string"

    /**
     * locality^^http://www.w3.org/2001/XMLSchema#string (property)
     * The locality (e.g. city or town) associated with the address of the object
     */
    lazy val `locality` = NS #> "locality"

    /**
     * url@en (property)
     * This object property has been deprecated^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `url` = NS #> "url"

    /**
     * has email^^http://www.w3.org/2001/XMLSchema#string (property)
     * To specify the electronic mail address for communication with the object^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `hasEmail` = NS #> "hasEmail"

    /**
     * Emergency^^http://www.w3.org/2001/XMLSchema#string (class)
     * 
     */
    lazy val `Emergency` = NS ## "Emergency"

    /**
     * extended address@en (property)
     * This data property has been deprecated
     */
    lazy val `extended-address` = NS #> "extended-address"

    /**
     * address@en (property)
     * This object property has been deprecated
     */
    lazy val `adr` = NS #> "adr"

    /**
     * telephone@en (property)
     * This object property has been deprecated
     */
    lazy val `tel` = NS #> "tel"

    /**
     * Gender^^http://www.w3.org/2001/XMLSchema#string (class)
     * Used for gender codes. The URI of the gender code must be used as the value for Gender.
     */
    lazy val `Gender` = NS ## "Gender"

    /**
     * Muse^^http://www.w3.org/2001/XMLSchema#string (class)
     * 
     */
    lazy val `Muse` = NS ## "Muse"

    /**
     * anniversary^^http://www.w3.org/2001/XMLSchema#string (property)
     * The date of marriage, or equivalent, of the object^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `anniversary` = NS #> "anniversary"

    /**
     * Spouse^^http://www.w3.org/2001/XMLSchema#string (class)
     * 
     */
    lazy val `Spouse` = NS ## "Spouse"

    /**
     * Crush^^http://www.w3.org/2001/XMLSchema#string (class)
     * 
     */
    lazy val `Crush` = NS ## "Crush"

    /**
     * has value@en (property)
     * Used to indicate the resource value of an object property that requires property parameters^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `hasValue` = NS #> "hasValue"

    /**
     * Kind^^http://www.w3.org/2001/XMLSchema#string (class)
     * The parent class for all objects^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `Kind` = NS ## "Kind"

    /**
     * Name^^http://www.w3.org/2001/XMLSchema#string (class)
     * To specify the components of the name of the object^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `Name` = NS ## "Name"

    /**
     * has formatted name@en (property)
     * Used to support property parameters for the formatted name data property^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `hasFN` = NS #> "hasFN"

    /**
     * Phone^^http://www.w3.org/2001/XMLSchema#string (class)
     * Used for telephone type codes. The URI of the telephone type code must be used as the value for the Telephone Type.
     */
    lazy val `TelephoneType` = NS ## "TelephoneType"

    /**
     * organization@en (property)
     * This object property has been deprecated. Use the organization-name data property.
     */
    lazy val `org` = NS #> "org"

    /**
     * Modem@en (class)
     * This class is deprecated
     */
    lazy val `Modem` = NS ## "Modem"

    /**
     * Male^^http://www.w3.org/2001/XMLSchema#string (class)
     * 
     */
    lazy val `Male` = NS ## "Male"

    /**
     * has language@en (property)
     * Used to support property parameters for the language data property^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `hasLanguage` = NS #> "hasLanguage"

    /**
     * has calendar request^^http://www.w3.org/2001/XMLSchema#string (property)
     * To specify the calendar user address to which a scheduling request be sent for the object. (Was called CALADRURI in RFC6350)^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `hasCalendarRequest` = NS #> "hasCalendarRequest"

    /**
     * PCS@en (class)
     * This class is deprecated
     */
    lazy val `PCS` = NS ## "PCS"

    /**
     * has organization unit name@en (property)
     * Used to support property parameters for the organization unit name data property^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `hasOrganizationUnit` = NS #> "hasOrganizationUnit"

    /**
     * photo@en (property)
     * This object property has been deprecated^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `photo` = NS #> "photo"

    /**
     * Work^^http://www.w3.org/2001/XMLSchema#string (class)
     * This implies that the property is related to an individual's work place^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `Work` = NS ## "Work"

    /**
     * has nickname@en (property)
     * Used to support property parameters for the nickname data property^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `hasNickname` = NS #> "hasNickname"

    /**
     * Text^^http://www.w3.org/2001/XMLSchema#string (class)
     * Also called sms telephone^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `Text` = NS ## "Text"

    /**
     * language@en (property)
     * To specify the language that may be used for contacting the object. May also be used as a property parameter.
     */
    lazy val `language` = NS #> "language"

    /**
     * Contact^^http://www.w3.org/2001/XMLSchema#string (class)
     * 
     */
    lazy val `Contact` = NS ## "Contact"

    /**
     * ISDN@en (class)
     * This class is deprecated
     */
    lazy val `ISDN` = NS ## "ISDN"

    /**
     * revision^^http://www.w3.org/2001/XMLSchema#string (property)
     * To specify revision information about the object
     */
    lazy val `rev` = NS #> "rev"

    /**
     * class@en (property)
     * This data property has been deprecated
     */
    lazy val `class` = NS #> "class"

    /**
     * honorific suffix^^http://www.w3.org/2001/XMLSchema#string (property)
     * The honorific suffix of the name associated with the object
     */
    lazy val `honorific-suffix` = NS #> "honorific-suffix"

    /**
     * None^^http://www.w3.org/2001/XMLSchema#string (class)
     * 
     */
    lazy val `None` = NS ## "None"

    /**
     * title^^http://www.w3.org/2001/XMLSchema#string (property)
     * To specify the position or job of the object
     */
    lazy val `title` = NS #> "title"

    /**
     * has telephone^^http://www.w3.org/2001/XMLSchema#string (property)
     * To specify the telephone number for telephony communication with the object^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `hasTelephone` = NS #> "hasTelephone"

    /**
     * Car@en (class)
     * This class is deprecated
     */
    lazy val `Car` = NS ## "Car"

    /**
     * has member^^http://www.w3.org/2001/XMLSchema#string (property)
     * To include a member in the group this object represents. (This property can only be used by Group individuals)^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `hasMember` = NS #> "hasMember"

    /**
     * honorific prefix^^http://www.w3.org/2001/XMLSchema#string (property)
     * The honorific prefix of the name associated with the object
     */
    lazy val `honorific-prefix` = NS #> "honorific-prefix"

    /**
     * country name^^http://www.w3.org/2001/XMLSchema#string (property)
     * The country name associated with the address of the object
     */
    lazy val `country-name` = NS #> "country-name"

    /**
     * region^^http://www.w3.org/2001/XMLSchema#string (property)
     * The region (e.g. state or province) associated with the address of the object
     */
    lazy val `region` = NS #> "region"

    /**
     * Date^^http://www.w3.org/2001/XMLSchema#string (class)
     * 
     */
    lazy val `Date` = NS ## "Date"

    /**
     * has honorific prefix@en (property)
     * Used to support property parameters for the honorific prefix data property^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `hasHonorificPrefix` = NS #> "hasHonorificPrefix"

    /**
     * mailer@en (property)
     * This data property has been deprecated
     */
    lazy val `mailer` = NS #> "mailer"

    /**
     * family name^^http://www.w3.org/2001/XMLSchema#string (property)
     * The family name associated with the object
     */
    lazy val `family-name` = NS #> "family-name"

    /**
     * has calendar link^^http://www.w3.org/2001/XMLSchema#string (property)
     * To specify the calendar associated with the object. (Was called CALURI in RFC6350)^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `hasCalendarLink` = NS #> "hasCalendarLink"

    /**
     * has family name@en (property)
     * Used to support property parameters for the family name data property^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `hasFamilyName` = NS #> "hasFamilyName"

    /**
     * additional name^^http://www.w3.org/2001/XMLSchema#string (property)
     * The additional name associated with the object
     */
    lazy val `additional-name` = NS #> "additional-name"

    /**
     * time zone^^http://www.w3.org/2001/XMLSchema#string (property)
     * To indicate time zone information that is specific to the object. May also be used as a property parameter.
     */
    lazy val `tz` = NS #> "tz"

    /**
     * has category@en (property)
     * Used to support property parameters for the category data property^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `hasCategory` = NS #> "hasCategory"

    /**
     * Group^^http://www.w3.org/2001/XMLSchema#string (class)
     * Object representing a group of persons or entities.  A group object will usually contain hasMember properties to specify the members of the group.^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `Group` = NS ## "Group"

    /**
     * BBS@en (class)
     * This class is deprecated
     */
    lazy val `BBS` = NS ## "BBS"

    /**
     * Internet@en (class)
     * This class is deprecated
     */
    lazy val `Internet` = NS ## "Internet"

    /**
     * note^^http://www.w3.org/2001/XMLSchema#string (property)
     * A note associated with the object
     */
    lazy val `note` = NS #> "note"

    /**
     * has key^^http://www.w3.org/2001/XMLSchema#string (property)
     * To specify a public key or authentication certificate associated with the object^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `hasKey` = NS #> "hasKey"

    /**
     * has photo^^http://www.w3.org/2001/XMLSchema#string (property)
     * To specify an image or photograph information that annotates some aspect of the object^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `hasPhoto` = NS #> "hasPhoto"

    /**
     * has additional name@en (property)
     * Used to support property parameters for the additional name data property^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `hasAdditionalName` = NS #> "hasAdditionalName"

    /**
     * organization name^^http://www.w3.org/2001/XMLSchema#string (property)
     * To specify the organizational name associated with the object
     */
    lazy val `organization-name` = NS #> "organization-name"

    /**
     * value@en (property)
     * Used to indicate the literal value of a data property that requires property parameters^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `value` = NS #> "value"

    /**
     * logo@en (property)
     * This object property has been deprecated^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `logo` = NS #> "logo"

    /**
     * has organization name@en (property)
     * Used to support property parameters for the organization name data property^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `hasOrganizationName` = NS #> "hasOrganizationName"

    /**
     * formatted name^^http://www.w3.org/2001/XMLSchema#string (property)
     * The formatted text corresponding to the name of the object
     */
    lazy val `fn` = NS #> "fn"

    /**
     * Individual^^http://www.w3.org/2001/XMLSchema#string (class)
     * An object representing a single person or entity^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `Individual` = NS ## "Individual"

    /**
     * Other^^http://www.w3.org/2001/XMLSchema#string (class)
     * 
     */
    lazy val `Other` = NS ## "Other"

    /**
     * Child^^http://www.w3.org/2001/XMLSchema#string (class)
     * 
     */
    lazy val `Child` = NS ## "Child"

    /**
     * Neighbor^^http://www.w3.org/2001/XMLSchema#string (class)
     * 
     */
    lazy val `Neighbor` = NS ## "Neighbor"

    /**
     * Intl@en (class)
     * This class is deprecated
     */
    lazy val `Intl` = NS ## "Intl"

    /**
     * has source@en (property)
     * To identify the source of directory information of the object
     */
    lazy val `hasSource` = NS #> "hasSource"

    /**
     * Fax^^http://www.w3.org/2001/XMLSchema#string (class)
     * 
     */
    lazy val `Fax` = NS ## "Fax"

    /**
     * W3C Ontology for vCard^^http://www.w3.org/2001/XMLSchema#string (class)
     * Ontology for vCard based on RFC6350^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `metadata` = NS ## "metadata"

    /**
     * birth date^^http://www.w3.org/2001/XMLSchema#string (property)
     * To specify the birth date of the object^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `bday` = NS #> "bday"

    /**
     * post office box@en (property)
     * This data property has been deprecated
     */
    lazy val `post-office-box` = NS #> "post-office-box"

    /**
     * has locality@en (property)
     * Used to support property parameters for the locality data property^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `hasLocality` = NS #> "hasLocality"

    /**
     * has region@en (property)
     * Used to support property parameters for the region data property^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `hasRegion` = NS #> "hasRegion"

    /**
     * Me^^http://www.w3.org/2001/XMLSchema#string (class)
     * 
     */
    lazy val `Me` = NS ## "Me"

    /**
     * product id^^http://www.w3.org/2001/XMLSchema#string (property)
     * To specify the identifier for the product that created the object
     */
    lazy val `prodid` = NS #> "prodid"

    /**
     * key@en (property)
     * This object property has been deprecated^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `key` = NS #> "key"

    /**
     * Postal@en (class)
     * This class is deprecated
     */
    lazy val `Postal` = NS ## "Postal"

    /**
     * Agent^^http://www.w3.org/2001/XMLSchema#string (class)
     * 
     */
    lazy val `Agent` = NS ## "Agent"

    /**
     * email^^http://www.w3.org/2001/XMLSchema#string (property)
     * This object property has been deprecated^^http://www.w3.org/2001/XMLSchema#string
     */
    lazy val `email` = NS #> "email"


}
