package core.ckan.database
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = scala.slick.driver.PostgresDriver
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: scala.slick.driver.JdbcProfile
  import profile.simple._
  import scala.slick.model.ForeignKeyAction
  import scala.slick.collection.heterogenous._
  import scala.slick.collection.heterogenous.syntax._
  import scala.slick.jdbc.{GetResult => GR}
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  
  /** Entity class storing rows of table Activity
   *  @param id Database column id PrimaryKey
   *  @param timestamp Database column timestamp 
   *  @param userId Database column user_id 
   *  @param objectId Database column object_id 
   *  @param revisionId Database column revision_id 
   *  @param activityType Database column activity_type 
   *  @param data Database column data  */
  case class ActivityRow(id: String, timestamp: Option[java.sql.Timestamp], userId: Option[String], objectId: Option[String], revisionId: Option[String], activityType: Option[String], data: Option[String])
  /** GetResult implicit for fetching ActivityRow objects using plain SQL queries */
  implicit def GetResultActivityRow(implicit e0: GR[String], e1: GR[java.sql.Timestamp]): GR[ActivityRow] = GR{
    prs => import prs._
    ActivityRow.tupled((<<[String], <<?[java.sql.Timestamp], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String]))
  }
  /** Table description of table activity. Objects of this class serve as prototypes for rows in queries. */
  class Activity(tag: Tag) extends Table[ActivityRow](tag, "activity") {
    def * = (id, timestamp, userId, objectId, revisionId, activityType, data) <> (ActivityRow.tupled, ActivityRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, timestamp, userId, objectId, revisionId, activityType, data).shaped.<>({r=>import r._; _1.map(_=> ActivityRow.tupled((_1.get, _2, _3, _4, _5, _6, _7)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id PrimaryKey */
    val id: Column[String] = column[String]("id", O.PrimaryKey)
    /** Database column timestamp  */
    val timestamp: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("timestamp")
    /** Database column user_id  */
    val userId: Column[Option[String]] = column[Option[String]]("user_id")
    /** Database column object_id  */
    val objectId: Column[Option[String]] = column[Option[String]]("object_id")
    /** Database column revision_id  */
    val revisionId: Column[Option[String]] = column[Option[String]]("revision_id")
    /** Database column activity_type  */
    val activityType: Column[Option[String]] = column[Option[String]]("activity_type")
    /** Database column data  */
    val data: Column[Option[String]] = column[Option[String]]("data")
  }
  /** Collection-like TableQuery object for table Activity */
  lazy val Activity = new TableQuery(tag => new Activity(tag))
  
  /** Entity class storing rows of table ActivityDetail
   *  @param id Database column id PrimaryKey
   *  @param activityId Database column activity_id 
   *  @param objectId Database column object_id 
   *  @param objectType Database column object_type 
   *  @param activityType Database column activity_type 
   *  @param data Database column data  */
  case class ActivityDetailRow(id: String, activityId: Option[String], objectId: Option[String], objectType: Option[String], activityType: Option[String], data: Option[String])
  /** GetResult implicit for fetching ActivityDetailRow objects using plain SQL queries */
  implicit def GetResultActivityDetailRow(implicit e0: GR[String]): GR[ActivityDetailRow] = GR{
    prs => import prs._
    ActivityDetailRow.tupled((<<[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String]))
  }
  /** Table description of table activity_detail. Objects of this class serve as prototypes for rows in queries. */
  class ActivityDetail(tag: Tag) extends Table[ActivityDetailRow](tag, "activity_detail") {
    def * = (id, activityId, objectId, objectType, activityType, data) <> (ActivityDetailRow.tupled, ActivityDetailRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, activityId, objectId, objectType, activityType, data).shaped.<>({r=>import r._; _1.map(_=> ActivityDetailRow.tupled((_1.get, _2, _3, _4, _5, _6)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id PrimaryKey */
    val id: Column[String] = column[String]("id", O.PrimaryKey)
    /** Database column activity_id  */
    val activityId: Column[Option[String]] = column[Option[String]]("activity_id")
    /** Database column object_id  */
    val objectId: Column[Option[String]] = column[Option[String]]("object_id")
    /** Database column object_type  */
    val objectType: Column[Option[String]] = column[Option[String]]("object_type")
    /** Database column activity_type  */
    val activityType: Column[Option[String]] = column[Option[String]]("activity_type")
    /** Database column data  */
    val data: Column[Option[String]] = column[Option[String]]("data")
    
    /** Foreign key referencing Activity (database name activity_detail_activity_id_fkey) */
    val activityFk = foreignKey("activity_detail_activity_id_fkey", activityId, Activity)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table ActivityDetail */
  lazy val ActivityDetail = new TableQuery(tag => new ActivityDetail(tag))
  
  /** Entity class storing rows of table AuthorizationGroup
   *  @param id Database column id PrimaryKey
   *  @param name Database column name 
   *  @param created Database column created  */
  case class AuthorizationGroupRow(id: String, name: Option[String], created: Option[java.sql.Timestamp])
  /** GetResult implicit for fetching AuthorizationGroupRow objects using plain SQL queries */
  implicit def GetResultAuthorizationGroupRow(implicit e0: GR[String], e1: GR[java.sql.Timestamp]): GR[AuthorizationGroupRow] = GR{
    prs => import prs._
    AuthorizationGroupRow.tupled((<<[String], <<?[String], <<?[java.sql.Timestamp]))
  }
  /** Table description of table authorization_group. Objects of this class serve as prototypes for rows in queries. */
  class AuthorizationGroup(tag: Tag) extends Table[AuthorizationGroupRow](tag, "authorization_group") {
    def * = (id, name, created) <> (AuthorizationGroupRow.tupled, AuthorizationGroupRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, name, created).shaped.<>({r=>import r._; _1.map(_=> AuthorizationGroupRow.tupled((_1.get, _2, _3)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id PrimaryKey */
    val id: Column[String] = column[String]("id", O.PrimaryKey)
    /** Database column name  */
    val name: Column[Option[String]] = column[Option[String]]("name")
    /** Database column created  */
    val created: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("created")
  }
  /** Collection-like TableQuery object for table AuthorizationGroup */
  lazy val AuthorizationGroup = new TableQuery(tag => new AuthorizationGroup(tag))
  
  /** Entity class storing rows of table AuthorizationGroupRole
   *  @param userObjectRoleId Database column user_object_role_id PrimaryKey
   *  @param authorizationGroupId Database column authorization_group_id  */
  case class AuthorizationGroupRoleRow(userObjectRoleId: String, authorizationGroupId: Option[String])
  /** GetResult implicit for fetching AuthorizationGroupRoleRow objects using plain SQL queries */
  implicit def GetResultAuthorizationGroupRoleRow(implicit e0: GR[String]): GR[AuthorizationGroupRoleRow] = GR{
    prs => import prs._
    AuthorizationGroupRoleRow.tupled((<<[String], <<?[String]))
  }
  /** Table description of table authorization_group_role. Objects of this class serve as prototypes for rows in queries. */
  class AuthorizationGroupRole(tag: Tag) extends Table[AuthorizationGroupRoleRow](tag, "authorization_group_role") {
    def * = (userObjectRoleId, authorizationGroupId) <> (AuthorizationGroupRoleRow.tupled, AuthorizationGroupRoleRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (userObjectRoleId.?, authorizationGroupId).shaped.<>({r=>import r._; _1.map(_=> AuthorizationGroupRoleRow.tupled((_1.get, _2)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column user_object_role_id PrimaryKey */
    val userObjectRoleId: Column[String] = column[String]("user_object_role_id", O.PrimaryKey)
    /** Database column authorization_group_id  */
    val authorizationGroupId: Column[Option[String]] = column[Option[String]]("authorization_group_id")
    
    /** Foreign key referencing AuthorizationGroup (database name authorization_group_role_authorization_group_id_fkey) */
    val authorizationGroupFk = foreignKey("authorization_group_role_authorization_group_id_fkey", authorizationGroupId, AuthorizationGroup)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing UserObjectRole (database name authorization_group_role_user_object_role_id_fkey) */
    val userObjectRoleFk = foreignKey("authorization_group_role_user_object_role_id_fkey", userObjectRoleId, UserObjectRole)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table AuthorizationGroupRole */
  lazy val AuthorizationGroupRole = new TableQuery(tag => new AuthorizationGroupRole(tag))
  
  /** Entity class storing rows of table AuthorizationGroupUser
   *  @param authorizationGroupId Database column authorization_group_id 
   *  @param userId Database column user_id 
   *  @param id Database column id PrimaryKey */
  case class AuthorizationGroupUserRow(authorizationGroupId: String, userId: String, id: String)
  /** GetResult implicit for fetching AuthorizationGroupUserRow objects using plain SQL queries */
  implicit def GetResultAuthorizationGroupUserRow(implicit e0: GR[String]): GR[AuthorizationGroupUserRow] = GR{
    prs => import prs._
    AuthorizationGroupUserRow.tupled((<<[String], <<[String], <<[String]))
  }
  /** Table description of table authorization_group_user. Objects of this class serve as prototypes for rows in queries. */
  class AuthorizationGroupUser(tag: Tag) extends Table[AuthorizationGroupUserRow](tag, "authorization_group_user") {
    def * = (authorizationGroupId, userId, id) <> (AuthorizationGroupUserRow.tupled, AuthorizationGroupUserRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (authorizationGroupId.?, userId.?, id.?).shaped.<>({r=>import r._; _1.map(_=> AuthorizationGroupUserRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column authorization_group_id  */
    val authorizationGroupId: Column[String] = column[String]("authorization_group_id")
    /** Database column user_id  */
    val userId: Column[String] = column[String]("user_id")
    /** Database column id PrimaryKey */
    val id: Column[String] = column[String]("id", O.PrimaryKey)
    
    /** Foreign key referencing AuthorizationGroup (database name authorization_group_user_authorization_group_id_fkey) */
    val authorizationGroupFk = foreignKey("authorization_group_user_authorization_group_id_fkey", authorizationGroupId, AuthorizationGroup)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing User (database name authorization_group_user_user_id_fkey) */
    val userFk = foreignKey("authorization_group_user_user_id_fkey", userId, User)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table AuthorizationGroupUser */
  lazy val AuthorizationGroupUser = new TableQuery(tag => new AuthorizationGroupUser(tag))
  
  /** Entity class storing rows of table CeleryTaskmeta
   *  @param id Database column id PrimaryKey
   *  @param taskId Database column task_id 
   *  @param status Database column status 
   *  @param result Database column result 
   *  @param dateDone Database column date_done 
   *  @param traceback Database column traceback  */
  case class CeleryTaskmetaRow(id: Int, taskId: Option[String], status: Option[String], result: Option[java.sql.Blob], dateDone: Option[java.sql.Timestamp], traceback: Option[String])
  /** GetResult implicit for fetching CeleryTaskmetaRow objects using plain SQL queries */
  implicit def GetResultCeleryTaskmetaRow(implicit e0: GR[Int], e1: GR[String], e2: GR[java.sql.Blob], e3: GR[java.sql.Timestamp]): GR[CeleryTaskmetaRow] = GR{
    prs => import prs._
    CeleryTaskmetaRow.tupled((<<[Int], <<?[String], <<?[String], <<?[java.sql.Blob], <<?[java.sql.Timestamp], <<?[String]))
  }
  /** Table description of table celery_taskmeta. Objects of this class serve as prototypes for rows in queries. */
  class CeleryTaskmeta(tag: Tag) extends Table[CeleryTaskmetaRow](tag, "celery_taskmeta") {
    def * = (id, taskId, status, result, dateDone, traceback) <> (CeleryTaskmetaRow.tupled, CeleryTaskmetaRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, taskId, status, result, dateDone, traceback).shaped.<>({r=>import r._; _1.map(_=> CeleryTaskmetaRow.tupled((_1.get, _2, _3, _4, _5, _6)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id PrimaryKey */
    val id: Column[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column task_id  */
    val taskId: Column[Option[String]] = column[Option[String]]("task_id")
    /** Database column status  */
    val status: Column[Option[String]] = column[Option[String]]("status")
    /** Database column result  */
    val result: Column[Option[java.sql.Blob]] = column[Option[java.sql.Blob]]("result")
    /** Database column date_done  */
    val dateDone: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("date_done")
    /** Database column traceback  */
    val traceback: Column[Option[String]] = column[Option[String]]("traceback")
    
    /** Uniqueness Index over (taskId) (database name celery_taskmeta_task_id_key) */
    val index1 = index("celery_taskmeta_task_id_key", taskId, unique=true)
  }
  /** Collection-like TableQuery object for table CeleryTaskmeta */
  lazy val CeleryTaskmeta = new TableQuery(tag => new CeleryTaskmeta(tag))
  
  /** Entity class storing rows of table CeleryTasksetmeta
   *  @param id Database column id PrimaryKey
   *  @param tasksetId Database column taskset_id 
   *  @param result Database column result 
   *  @param dateDone Database column date_done  */
  case class CeleryTasksetmetaRow(id: Int, tasksetId: Option[String], result: Option[java.sql.Blob], dateDone: Option[java.sql.Timestamp])
  /** GetResult implicit for fetching CeleryTasksetmetaRow objects using plain SQL queries */
  implicit def GetResultCeleryTasksetmetaRow(implicit e0: GR[Int], e1: GR[String], e2: GR[java.sql.Blob], e3: GR[java.sql.Timestamp]): GR[CeleryTasksetmetaRow] = GR{
    prs => import prs._
    CeleryTasksetmetaRow.tupled((<<[Int], <<?[String], <<?[java.sql.Blob], <<?[java.sql.Timestamp]))
  }
  /** Table description of table celery_tasksetmeta. Objects of this class serve as prototypes for rows in queries. */
  class CeleryTasksetmeta(tag: Tag) extends Table[CeleryTasksetmetaRow](tag, "celery_tasksetmeta") {
    def * = (id, tasksetId, result, dateDone) <> (CeleryTasksetmetaRow.tupled, CeleryTasksetmetaRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, tasksetId, result, dateDone).shaped.<>({r=>import r._; _1.map(_=> CeleryTasksetmetaRow.tupled((_1.get, _2, _3, _4)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id PrimaryKey */
    val id: Column[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column taskset_id  */
    val tasksetId: Column[Option[String]] = column[Option[String]]("taskset_id")
    /** Database column result  */
    val result: Column[Option[java.sql.Blob]] = column[Option[java.sql.Blob]]("result")
    /** Database column date_done  */
    val dateDone: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("date_done")
    
    /** Uniqueness Index over (tasksetId) (database name celery_tasksetmeta_taskset_id_key) */
    val index1 = index("celery_tasksetmeta_taskset_id_key", tasksetId, unique=true)
  }
  /** Collection-like TableQuery object for table CeleryTasksetmeta */
  lazy val CeleryTasksetmeta = new TableQuery(tag => new CeleryTasksetmeta(tag))
  
  /** Entity class storing rows of table Dashboard
   *  @param userId Database column user_id PrimaryKey
   *  @param activityStreamLastViewed Database column activity_stream_last_viewed 
   *  @param emailLastSent Database column email_last_sent  */
  case class DashboardRow(userId: String, activityStreamLastViewed: java.sql.Timestamp, emailLastSent: java.sql.Timestamp)
  /** GetResult implicit for fetching DashboardRow objects using plain SQL queries */
  implicit def GetResultDashboardRow(implicit e0: GR[String], e1: GR[java.sql.Timestamp]): GR[DashboardRow] = GR{
    prs => import prs._
    DashboardRow.tupled((<<[String], <<[java.sql.Timestamp], <<[java.sql.Timestamp]))
  }
  /** Table description of table dashboard. Objects of this class serve as prototypes for rows in queries. */
  class Dashboard(tag: Tag) extends Table[DashboardRow](tag, "dashboard") {
    def * = (userId, activityStreamLastViewed, emailLastSent) <> (DashboardRow.tupled, DashboardRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (userId.?, activityStreamLastViewed.?, emailLastSent.?).shaped.<>({r=>import r._; _1.map(_=> DashboardRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column user_id PrimaryKey */
    val userId: Column[String] = column[String]("user_id", O.PrimaryKey)
    /** Database column activity_stream_last_viewed  */
    val activityStreamLastViewed: Column[java.sql.Timestamp] = column[java.sql.Timestamp]("activity_stream_last_viewed")
    /** Database column email_last_sent  */
    val emailLastSent: Column[java.sql.Timestamp] = column[java.sql.Timestamp]("email_last_sent")
    
    /** Foreign key referencing User (database name dashboard_user_id_fkey) */
    val userFk = foreignKey("dashboard_user_id_fkey", userId, User)(r => r.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table Dashboard */
  lazy val Dashboard = new TableQuery(tag => new Dashboard(tag))
  
  /** Entity class storing rows of table Group
   *  @param id Database column id PrimaryKey
   *  @param name Database column name 
   *  @param title Database column title 
   *  @param description Database column description 
   *  @param created Database column created 
   *  @param state Database column state 
   *  @param revisionId Database column revision_id 
   *  @param `type` Database column type 
   *  @param approvalStatus Database column approval_status 
   *  @param imageUrl Database column image_url 
   *  @param isOrganization Database column is_organization Default(false) */
  case class GroupRow(id: String, name: String, title: Option[String], description: Option[String], created: Option[java.sql.Timestamp], state: Option[String], revisionId: Option[String], `type`: String, approvalStatus: Option[String], imageUrl: Option[String], isOrganization: Option[Boolean]=false)
  /** GetResult implicit for fetching GroupRow objects using plain SQL queries */
  implicit def GetResultGroupRow(implicit e0: GR[String], e1: GR[java.sql.Timestamp], e2: GR[Boolean]): GR[GroupRow] = GR{
    prs => import prs._
    GroupRow.tupled((<<[String], <<[String], <<?[String], <<?[String], <<?[java.sql.Timestamp], <<?[String], <<?[String], <<[String], <<?[String], <<?[String], <<?[Boolean]))
  }
  /** Table description of table group. Objects of this class serve as prototypes for rows in queries.
   *  NOTE: The following names collided with Scala keywords and were escaped: type */
  class Group(tag: Tag) extends Table[GroupRow](tag, "group") {
    def * = (id, name, title, description, created, state, revisionId, `type`, approvalStatus, imageUrl, isOrganization) <> (GroupRow.tupled, GroupRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, name.?, title, description, created, state, revisionId, `type`.?, approvalStatus, imageUrl, isOrganization).shaped.<>({r=>import r._; _1.map(_=> GroupRow.tupled((_1.get, _2.get, _3, _4, _5, _6, _7, _8.get, _9, _10, _11)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id PrimaryKey */
    val id: Column[String] = column[String]("id", O.PrimaryKey)
    /** Database column name  */
    val name: Column[String] = column[String]("name")
    /** Database column title  */
    val title: Column[Option[String]] = column[Option[String]]("title")
    /** Database column description  */
    val description: Column[Option[String]] = column[Option[String]]("description")
    /** Database column created  */
    val created: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("created")
    /** Database column state  */
    val state: Column[Option[String]] = column[Option[String]]("state")
    /** Database column revision_id  */
    val revisionId: Column[Option[String]] = column[Option[String]]("revision_id")
    /** Database column type 
     *  NOTE: The name was escaped because it collided with a Scala keyword. */
    val `type`: Column[String] = column[String]("type")
    /** Database column approval_status  */
    val approvalStatus: Column[Option[String]] = column[Option[String]]("approval_status")
    /** Database column image_url  */
    val imageUrl: Column[Option[String]] = column[Option[String]]("image_url")
    /** Database column is_organization Default(false) */
    val isOrganization: Column[Option[Boolean]] = column[Option[Boolean]]("is_organization", O.Default(false))
    
    /** Foreign key referencing Revision (database name group_revision_id_fkey) */
    val revisionFk = foreignKey("group_revision_id_fkey", revisionId, Revision)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    
    /** Uniqueness Index over (name) (database name group_name_key) */
    val index1 = index("group_name_key", name, unique=true)
    /** Index over (id) (database name idx_group_id) */
    val index2 = index("idx_group_id", id)
    /** Index over (name) (database name idx_group_name) */
    val index3 = index("idx_group_name", name)
  }
  /** Collection-like TableQuery object for table Group */
  lazy val Group = new TableQuery(tag => new Group(tag))
  
  /** Entity class storing rows of table GroupExtra
   *  @param id Database column id PrimaryKey
   *  @param groupId Database column group_id 
   *  @param key Database column key 
   *  @param value Database column value 
   *  @param state Database column state 
   *  @param revisionId Database column revision_id  */
  case class GroupExtraRow(id: String, groupId: Option[String], key: Option[String], value: Option[String], state: Option[String], revisionId: Option[String])
  /** GetResult implicit for fetching GroupExtraRow objects using plain SQL queries */
  implicit def GetResultGroupExtraRow(implicit e0: GR[String]): GR[GroupExtraRow] = GR{
    prs => import prs._
    GroupExtraRow.tupled((<<[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String]))
  }
  /** Table description of table group_extra. Objects of this class serve as prototypes for rows in queries. */
  class GroupExtra(tag: Tag) extends Table[GroupExtraRow](tag, "group_extra") {
    def * = (id, groupId, key, value, state, revisionId) <> (GroupExtraRow.tupled, GroupExtraRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, groupId, key, value, state, revisionId).shaped.<>({r=>import r._; _1.map(_=> GroupExtraRow.tupled((_1.get, _2, _3, _4, _5, _6)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id PrimaryKey */
    val id: Column[String] = column[String]("id", O.PrimaryKey)
    /** Database column group_id  */
    val groupId: Column[Option[String]] = column[Option[String]]("group_id")
    /** Database column key  */
    val key: Column[Option[String]] = column[Option[String]]("key")
    /** Database column value  */
    val value: Column[Option[String]] = column[Option[String]]("value")
    /** Database column state  */
    val state: Column[Option[String]] = column[Option[String]]("state")
    /** Database column revision_id  */
    val revisionId: Column[Option[String]] = column[Option[String]]("revision_id")
    
    /** Foreign key referencing Group (database name group_extra_group_id_fkey) */
    val groupFk = foreignKey("group_extra_group_id_fkey", groupId, Group)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Revision (database name group_extra_revision_id_fkey) */
    val revisionFk = foreignKey("group_extra_revision_id_fkey", revisionId, Revision)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table GroupExtra */
  lazy val GroupExtra = new TableQuery(tag => new GroupExtra(tag))
  
  /** Entity class storing rows of table GroupExtraRevision
   *  @param id Database column id 
   *  @param groupId Database column group_id 
   *  @param key Database column key 
   *  @param value Database column value 
   *  @param state Database column state 
   *  @param revisionId Database column revision_id 
   *  @param continuityId Database column continuity_id 
   *  @param expiredId Database column expired_id 
   *  @param revisionTimestamp Database column revision_timestamp 
   *  @param expiredTimestamp Database column expired_timestamp 
   *  @param current Database column current  */
  case class GroupExtraRevisionRow(id: String, groupId: Option[String], key: Option[String], value: Option[String], state: Option[String], revisionId: String, continuityId: Option[String], expiredId: Option[String], revisionTimestamp: Option[java.sql.Timestamp], expiredTimestamp: Option[java.sql.Timestamp], current: Option[Boolean])
  /** GetResult implicit for fetching GroupExtraRevisionRow objects using plain SQL queries */
  implicit def GetResultGroupExtraRevisionRow(implicit e0: GR[String], e1: GR[java.sql.Timestamp], e2: GR[Boolean]): GR[GroupExtraRevisionRow] = GR{
    prs => import prs._
    GroupExtraRevisionRow.tupled((<<[String], <<?[String], <<?[String], <<?[String], <<?[String], <<[String], <<?[String], <<?[String], <<?[java.sql.Timestamp], <<?[java.sql.Timestamp], <<?[Boolean]))
  }
  /** Table description of table group_extra_revision. Objects of this class serve as prototypes for rows in queries. */
  class GroupExtraRevision(tag: Tag) extends Table[GroupExtraRevisionRow](tag, "group_extra_revision") {
    def * = (id, groupId, key, value, state, revisionId, continuityId, expiredId, revisionTimestamp, expiredTimestamp, current) <> (GroupExtraRevisionRow.tupled, GroupExtraRevisionRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, groupId, key, value, state, revisionId.?, continuityId, expiredId, revisionTimestamp, expiredTimestamp, current).shaped.<>({r=>import r._; _1.map(_=> GroupExtraRevisionRow.tupled((_1.get, _2, _3, _4, _5, _6.get, _7, _8, _9, _10, _11)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id  */
    val id: Column[String] = column[String]("id")
    /** Database column group_id  */
    val groupId: Column[Option[String]] = column[Option[String]]("group_id")
    /** Database column key  */
    val key: Column[Option[String]] = column[Option[String]]("key")
    /** Database column value  */
    val value: Column[Option[String]] = column[Option[String]]("value")
    /** Database column state  */
    val state: Column[Option[String]] = column[Option[String]]("state")
    /** Database column revision_id  */
    val revisionId: Column[String] = column[String]("revision_id")
    /** Database column continuity_id  */
    val continuityId: Column[Option[String]] = column[Option[String]]("continuity_id")
    /** Database column expired_id  */
    val expiredId: Column[Option[String]] = column[Option[String]]("expired_id")
    /** Database column revision_timestamp  */
    val revisionTimestamp: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("revision_timestamp")
    /** Database column expired_timestamp  */
    val expiredTimestamp: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("expired_timestamp")
    /** Database column current  */
    val current: Column[Option[Boolean]] = column[Option[Boolean]]("current")
    
    /** Primary key of GroupExtraRevision (database name group_extra_revision_pkey) */
    val pk = primaryKey("group_extra_revision_pkey", (id, revisionId))
    
    /** Foreign key referencing Group (database name group_extra_revision_group_id_fkey) */
    val groupFk = foreignKey("group_extra_revision_group_id_fkey", groupId, Group)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing GroupExtra (database name group_extra_revision_continuity_id_fkey) */
    val groupExtraFk = foreignKey("group_extra_revision_continuity_id_fkey", continuityId, GroupExtra)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Revision (database name group_extra_revision_revision_id_fkey) */
    val revisionFk = foreignKey("group_extra_revision_revision_id_fkey", revisionId, Revision)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    
    /** Index over (current) (database name idx_group_extra_current) */
    val index1 = index("idx_group_extra_current", current)
    /** Index over (revisionTimestamp,expiredTimestamp,id) (database name idx_group_extra_period) */
    val index2 = index("idx_group_extra_period", (revisionTimestamp, expiredTimestamp, id))
    /** Index over (revisionTimestamp,expiredTimestamp,groupId) (database name idx_group_extra_period_group) */
    val index3 = index("idx_group_extra_period_group", (revisionTimestamp, expiredTimestamp, groupId))
  }
  /** Collection-like TableQuery object for table GroupExtraRevision */
  lazy val GroupExtraRevision = new TableQuery(tag => new GroupExtraRevision(tag))
  
  /** Entity class storing rows of table GroupRevision
   *  @param id Database column id 
   *  @param name Database column name 
   *  @param title Database column title 
   *  @param description Database column description 
   *  @param created Database column created 
   *  @param state Database column state 
   *  @param revisionId Database column revision_id 
   *  @param continuityId Database column continuity_id 
   *  @param expiredId Database column expired_id 
   *  @param revisionTimestamp Database column revision_timestamp 
   *  @param expiredTimestamp Database column expired_timestamp 
   *  @param current Database column current 
   *  @param `type` Database column type 
   *  @param approvalStatus Database column approval_status 
   *  @param imageUrl Database column image_url 
   *  @param isOrganization Database column is_organization Default(false) */
  case class GroupRevisionRow(id: String, name: String, title: Option[String], description: Option[String], created: Option[java.sql.Timestamp], state: Option[String], revisionId: String, continuityId: Option[String], expiredId: Option[String], revisionTimestamp: Option[java.sql.Timestamp], expiredTimestamp: Option[java.sql.Timestamp], current: Option[Boolean], `type`: String, approvalStatus: Option[String], imageUrl: Option[String], isOrganization: Option[Boolean]=false)
  /** GetResult implicit for fetching GroupRevisionRow objects using plain SQL queries */
  implicit def GetResultGroupRevisionRow(implicit e0: GR[String], e1: GR[java.sql.Timestamp], e2: GR[Boolean]): GR[GroupRevisionRow] = GR{
    prs => import prs._
    GroupRevisionRow.tupled((<<[String], <<[String], <<?[String], <<?[String], <<?[java.sql.Timestamp], <<?[String], <<[String], <<?[String], <<?[String], <<?[java.sql.Timestamp], <<?[java.sql.Timestamp], <<?[Boolean], <<[String], <<?[String], <<?[String], <<?[Boolean]))
  }
  /** Table description of table group_revision. Objects of this class serve as prototypes for rows in queries.
   *  NOTE: The following names collided with Scala keywords and were escaped: type */
  class GroupRevision(tag: Tag) extends Table[GroupRevisionRow](tag, "group_revision") {
    def * = (id, name, title, description, created, state, revisionId, continuityId, expiredId, revisionTimestamp, expiredTimestamp, current, `type`, approvalStatus, imageUrl, isOrganization) <> (GroupRevisionRow.tupled, GroupRevisionRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, name.?, title, description, created, state, revisionId.?, continuityId, expiredId, revisionTimestamp, expiredTimestamp, current, `type`.?, approvalStatus, imageUrl, isOrganization).shaped.<>({r=>import r._; _1.map(_=> GroupRevisionRow.tupled((_1.get, _2.get, _3, _4, _5, _6, _7.get, _8, _9, _10, _11, _12, _13.get, _14, _15, _16)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id  */
    val id: Column[String] = column[String]("id")
    /** Database column name  */
    val name: Column[String] = column[String]("name")
    /** Database column title  */
    val title: Column[Option[String]] = column[Option[String]]("title")
    /** Database column description  */
    val description: Column[Option[String]] = column[Option[String]]("description")
    /** Database column created  */
    val created: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("created")
    /** Database column state  */
    val state: Column[Option[String]] = column[Option[String]]("state")
    /** Database column revision_id  */
    val revisionId: Column[String] = column[String]("revision_id")
    /** Database column continuity_id  */
    val continuityId: Column[Option[String]] = column[Option[String]]("continuity_id")
    /** Database column expired_id  */
    val expiredId: Column[Option[String]] = column[Option[String]]("expired_id")
    /** Database column revision_timestamp  */
    val revisionTimestamp: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("revision_timestamp")
    /** Database column expired_timestamp  */
    val expiredTimestamp: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("expired_timestamp")
    /** Database column current  */
    val current: Column[Option[Boolean]] = column[Option[Boolean]]("current")
    /** Database column type 
     *  NOTE: The name was escaped because it collided with a Scala keyword. */
    val `type`: Column[String] = column[String]("type")
    /** Database column approval_status  */
    val approvalStatus: Column[Option[String]] = column[Option[String]]("approval_status")
    /** Database column image_url  */
    val imageUrl: Column[Option[String]] = column[Option[String]]("image_url")
    /** Database column is_organization Default(false) */
    val isOrganization: Column[Option[Boolean]] = column[Option[Boolean]]("is_organization", O.Default(false))
    
    /** Primary key of GroupRevision (database name group_revision_pkey) */
    val pk = primaryKey("group_revision_pkey", (id, revisionId))
    
    /** Foreign key referencing Group (database name group_revision_continuity_id_fkey) */
    val groupFk = foreignKey("group_revision_continuity_id_fkey", continuityId, Group)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Revision (database name group_revision_revision_id_fkey) */
    val revisionFk = foreignKey("group_revision_revision_id_fkey", revisionId, Revision)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    
    /** Index over (current) (database name idx_group_current) */
    val index1 = index("idx_group_current", current)
    /** Index over (revisionTimestamp,expiredTimestamp,id) (database name idx_group_period) */
    val index2 = index("idx_group_period", (revisionTimestamp, expiredTimestamp, id))
  }
  /** Collection-like TableQuery object for table GroupRevision */
  lazy val GroupRevision = new TableQuery(tag => new GroupRevision(tag))
  
  /** Entity class storing rows of table GroupRole
   *  @param userObjectRoleId Database column user_object_role_id PrimaryKey
   *  @param groupId Database column group_id  */
  case class GroupRoleRow(userObjectRoleId: String, groupId: Option[String])
  /** GetResult implicit for fetching GroupRoleRow objects using plain SQL queries */
  implicit def GetResultGroupRoleRow(implicit e0: GR[String]): GR[GroupRoleRow] = GR{
    prs => import prs._
    GroupRoleRow.tupled((<<[String], <<?[String]))
  }
  /** Table description of table group_role. Objects of this class serve as prototypes for rows in queries. */
  class GroupRole(tag: Tag) extends Table[GroupRoleRow](tag, "group_role") {
    def * = (userObjectRoleId, groupId) <> (GroupRoleRow.tupled, GroupRoleRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (userObjectRoleId.?, groupId).shaped.<>({r=>import r._; _1.map(_=> GroupRoleRow.tupled((_1.get, _2)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column user_object_role_id PrimaryKey */
    val userObjectRoleId: Column[String] = column[String]("user_object_role_id", O.PrimaryKey)
    /** Database column group_id  */
    val groupId: Column[Option[String]] = column[Option[String]]("group_id")
    
    /** Foreign key referencing Group (database name group_role_group_id_fkey) */
    val groupFk = foreignKey("group_role_group_id_fkey", groupId, Group)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing UserObjectRole (database name group_role_user_object_role_id_fkey) */
    val userObjectRoleFk = foreignKey("group_role_user_object_role_id_fkey", userObjectRoleId, UserObjectRole)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table GroupRole */
  lazy val GroupRole = new TableQuery(tag => new GroupRole(tag))
  
  /** Entity class storing rows of table HarvestGatherError
   *  @param id Database column id PrimaryKey
   *  @param harvestJobId Database column harvest_job_id 
   *  @param message Database column message 
   *  @param created Database column created  */
  case class HarvestGatherErrorRow(id: String, harvestJobId: Option[String], message: Option[String], created: Option[java.sql.Timestamp])
  /** GetResult implicit for fetching HarvestGatherErrorRow objects using plain SQL queries */
  implicit def GetResultHarvestGatherErrorRow(implicit e0: GR[String], e1: GR[java.sql.Timestamp]): GR[HarvestGatherErrorRow] = GR{
    prs => import prs._
    HarvestGatherErrorRow.tupled((<<[String], <<?[String], <<?[String], <<?[java.sql.Timestamp]))
  }
  /** Table description of table harvest_gather_error. Objects of this class serve as prototypes for rows in queries. */
  class HarvestGatherError(tag: Tag) extends Table[HarvestGatherErrorRow](tag, "harvest_gather_error") {
    def * = (id, harvestJobId, message, created) <> (HarvestGatherErrorRow.tupled, HarvestGatherErrorRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, harvestJobId, message, created).shaped.<>({r=>import r._; _1.map(_=> HarvestGatherErrorRow.tupled((_1.get, _2, _3, _4)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id PrimaryKey */
    val id: Column[String] = column[String]("id", O.PrimaryKey)
    /** Database column harvest_job_id  */
    val harvestJobId: Column[Option[String]] = column[Option[String]]("harvest_job_id")
    /** Database column message  */
    val message: Column[Option[String]] = column[Option[String]]("message")
    /** Database column created  */
    val created: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("created")
    
    /** Foreign key referencing HarvestJob (database name harvest_gather_error_harvest_job_id_fkey) */
    val harvestJobFk = foreignKey("harvest_gather_error_harvest_job_id_fkey", harvestJobId, HarvestJob)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table HarvestGatherError */
  lazy val HarvestGatherError = new TableQuery(tag => new HarvestGatherError(tag))
  
  /** Entity class storing rows of table HarvestJob
   *  @param id Database column id PrimaryKey
   *  @param created Database column created 
   *  @param gatherStarted Database column gather_started 
   *  @param gatherFinished Database column gather_finished 
   *  @param finished Database column finished 
   *  @param sourceId Database column source_id 
   *  @param status Database column status 
   *  @param config Database column config  */
  case class HarvestJobRow(id: String, created: Option[java.sql.Timestamp], gatherStarted: Option[java.sql.Timestamp], gatherFinished: Option[java.sql.Timestamp], finished: Option[java.sql.Timestamp], sourceId: Option[String], status: String, config: Option[String])
  /** GetResult implicit for fetching HarvestJobRow objects using plain SQL queries */
  implicit def GetResultHarvestJobRow(implicit e0: GR[String], e1: GR[java.sql.Timestamp]): GR[HarvestJobRow] = GR{
    prs => import prs._
    HarvestJobRow.tupled((<<[String], <<?[java.sql.Timestamp], <<?[java.sql.Timestamp], <<?[java.sql.Timestamp], <<?[java.sql.Timestamp], <<?[String], <<[String], <<?[String]))
  }
  /** Table description of table harvest_job. Objects of this class serve as prototypes for rows in queries. */
  class HarvestJob(tag: Tag) extends Table[HarvestJobRow](tag, "harvest_job") {
    def * = (id, created, gatherStarted, gatherFinished, finished, sourceId, status, config) <> (HarvestJobRow.tupled, HarvestJobRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, created, gatherStarted, gatherFinished, finished, sourceId, status.?, config).shaped.<>({r=>import r._; _1.map(_=> HarvestJobRow.tupled((_1.get, _2, _3, _4, _5, _6, _7.get, _8)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id PrimaryKey */
    val id: Column[String] = column[String]("id", O.PrimaryKey)
    /** Database column created  */
    val created: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("created")
    /** Database column gather_started  */
    val gatherStarted: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("gather_started")
    /** Database column gather_finished  */
    val gatherFinished: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("gather_finished")
    /** Database column finished  */
    val finished: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("finished")
    /** Database column source_id  */
    val sourceId: Column[Option[String]] = column[Option[String]]("source_id")
    /** Database column status  */
    val status: Column[String] = column[String]("status")
    /** Database column config  */
    val config: Column[Option[String]] = column[Option[String]]("config")
    
    /** Foreign key referencing HarvestSource (database name harvest_job_source_id_fkey) */
    val harvestSourceFk = foreignKey("harvest_job_source_id_fkey", sourceId, HarvestSource)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table HarvestJob */
  lazy val HarvestJob = new TableQuery(tag => new HarvestJob(tag))
  
  /** Entity class storing rows of table HarvestObject
   *  @param id Database column id PrimaryKey
   *  @param guid Database column guid 
   *  @param current Database column current 
   *  @param gathered Database column gathered 
   *  @param fetchStarted Database column fetch_started 
   *  @param content Database column content 
   *  @param fetchFinished Database column fetch_finished 
   *  @param importStarted Database column import_started 
   *  @param importFinished Database column import_finished 
   *  @param state Database column state 
   *  @param metadataModifiedDate Database column metadata_modified_date 
   *  @param retryTimes Database column retry_times 
   *  @param harvestJobId Database column harvest_job_id 
   *  @param harvestSourceId Database column harvest_source_id 
   *  @param packageId Database column package_id 
   *  @param reportStatus Database column report_status  */
  case class HarvestObjectRow(id: String, guid: Option[String], current: Option[Boolean], gathered: Option[java.sql.Timestamp], fetchStarted: Option[java.sql.Timestamp], content: Option[String], fetchFinished: Option[java.sql.Timestamp], importStarted: Option[java.sql.Timestamp], importFinished: Option[java.sql.Timestamp], state: Option[String], metadataModifiedDate: Option[java.sql.Timestamp], retryTimes: Option[Int], harvestJobId: Option[String], harvestSourceId: Option[String], packageId: Option[String], reportStatus: Option[String])
  /** GetResult implicit for fetching HarvestObjectRow objects using plain SQL queries */
  implicit def GetResultHarvestObjectRow(implicit e0: GR[String], e1: GR[Boolean], e2: GR[java.sql.Timestamp], e3: GR[Int]): GR[HarvestObjectRow] = GR{
    prs => import prs._
    HarvestObjectRow.tupled((<<[String], <<?[String], <<?[Boolean], <<?[java.sql.Timestamp], <<?[java.sql.Timestamp], <<?[String], <<?[java.sql.Timestamp], <<?[java.sql.Timestamp], <<?[java.sql.Timestamp], <<?[String], <<?[java.sql.Timestamp], <<?[Int], <<?[String], <<?[String], <<?[String], <<?[String]))
  }
  /** Table description of table harvest_object. Objects of this class serve as prototypes for rows in queries. */
  class HarvestObject(tag: Tag) extends Table[HarvestObjectRow](tag, "harvest_object") {
    def * = (id, guid, current, gathered, fetchStarted, content, fetchFinished, importStarted, importFinished, state, metadataModifiedDate, retryTimes, harvestJobId, harvestSourceId, packageId, reportStatus) <> (HarvestObjectRow.tupled, HarvestObjectRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, guid, current, gathered, fetchStarted, content, fetchFinished, importStarted, importFinished, state, metadataModifiedDate, retryTimes, harvestJobId, harvestSourceId, packageId, reportStatus).shaped.<>({r=>import r._; _1.map(_=> HarvestObjectRow.tupled((_1.get, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id PrimaryKey */
    val id: Column[String] = column[String]("id", O.PrimaryKey)
    /** Database column guid  */
    val guid: Column[Option[String]] = column[Option[String]]("guid")
    /** Database column current  */
    val current: Column[Option[Boolean]] = column[Option[Boolean]]("current")
    /** Database column gathered  */
    val gathered: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("gathered")
    /** Database column fetch_started  */
    val fetchStarted: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("fetch_started")
    /** Database column content  */
    val content: Column[Option[String]] = column[Option[String]]("content")
    /** Database column fetch_finished  */
    val fetchFinished: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("fetch_finished")
    /** Database column import_started  */
    val importStarted: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("import_started")
    /** Database column import_finished  */
    val importFinished: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("import_finished")
    /** Database column state  */
    val state: Column[Option[String]] = column[Option[String]]("state")
    /** Database column metadata_modified_date  */
    val metadataModifiedDate: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("metadata_modified_date")
    /** Database column retry_times  */
    val retryTimes: Column[Option[Int]] = column[Option[Int]]("retry_times")
    /** Database column harvest_job_id  */
    val harvestJobId: Column[Option[String]] = column[Option[String]]("harvest_job_id")
    /** Database column harvest_source_id  */
    val harvestSourceId: Column[Option[String]] = column[Option[String]]("harvest_source_id")
    /** Database column package_id  */
    val packageId: Column[Option[String]] = column[Option[String]]("package_id")
    /** Database column report_status  */
    val reportStatus: Column[Option[String]] = column[Option[String]]("report_status")
    
    /** Foreign key referencing HarvestJob (database name harvest_object_harvest_job_id_fkey) */
    val harvestJobFk = foreignKey("harvest_object_harvest_job_id_fkey", harvestJobId, HarvestJob)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing HarvestSource (database name harvest_object_harvest_source_id_fkey) */
    val harvestSourceFk = foreignKey("harvest_object_harvest_source_id_fkey", harvestSourceId, HarvestSource)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Package (database name harvest_object_package_id_fkey) */
    val packageFk = foreignKey("harvest_object_package_id_fkey", packageId, Package)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table HarvestObject */
  lazy val HarvestObject = new TableQuery(tag => new HarvestObject(tag))
  
  /** Entity class storing rows of table HarvestObjectError
   *  @param id Database column id PrimaryKey
   *  @param harvestObjectId Database column harvest_object_id 
   *  @param message Database column message 
   *  @param stage Database column stage 
   *  @param line Database column line 
   *  @param created Database column created  */
  case class HarvestObjectErrorRow(id: String, harvestObjectId: Option[String], message: Option[String], stage: Option[String], line: Option[Int], created: Option[java.sql.Timestamp])
  /** GetResult implicit for fetching HarvestObjectErrorRow objects using plain SQL queries */
  implicit def GetResultHarvestObjectErrorRow(implicit e0: GR[String], e1: GR[Int], e2: GR[java.sql.Timestamp]): GR[HarvestObjectErrorRow] = GR{
    prs => import prs._
    HarvestObjectErrorRow.tupled((<<[String], <<?[String], <<?[String], <<?[String], <<?[Int], <<?[java.sql.Timestamp]))
  }
  /** Table description of table harvest_object_error. Objects of this class serve as prototypes for rows in queries. */
  class HarvestObjectError(tag: Tag) extends Table[HarvestObjectErrorRow](tag, "harvest_object_error") {
    def * = (id, harvestObjectId, message, stage, line, created) <> (HarvestObjectErrorRow.tupled, HarvestObjectErrorRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, harvestObjectId, message, stage, line, created).shaped.<>({r=>import r._; _1.map(_=> HarvestObjectErrorRow.tupled((_1.get, _2, _3, _4, _5, _6)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id PrimaryKey */
    val id: Column[String] = column[String]("id", O.PrimaryKey)
    /** Database column harvest_object_id  */
    val harvestObjectId: Column[Option[String]] = column[Option[String]]("harvest_object_id")
    /** Database column message  */
    val message: Column[Option[String]] = column[Option[String]]("message")
    /** Database column stage  */
    val stage: Column[Option[String]] = column[Option[String]]("stage")
    /** Database column line  */
    val line: Column[Option[Int]] = column[Option[Int]]("line")
    /** Database column created  */
    val created: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("created")
    
    /** Foreign key referencing HarvestObject (database name harvest_object_error_harvest_object_id_fkey) */
    val harvestObjectFk = foreignKey("harvest_object_error_harvest_object_id_fkey", harvestObjectId, HarvestObject)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table HarvestObjectError */
  lazy val HarvestObjectError = new TableQuery(tag => new HarvestObjectError(tag))
  
  /** Entity class storing rows of table HarvestObjectExtra
   *  @param id Database column id PrimaryKey
   *  @param harvestObjectId Database column harvest_object_id 
   *  @param key Database column key 
   *  @param value Database column value  */
  case class HarvestObjectExtraRow(id: String, harvestObjectId: Option[String], key: Option[String], value: Option[String])
  /** GetResult implicit for fetching HarvestObjectExtraRow objects using plain SQL queries */
  implicit def GetResultHarvestObjectExtraRow(implicit e0: GR[String]): GR[HarvestObjectExtraRow] = GR{
    prs => import prs._
    HarvestObjectExtraRow.tupled((<<[String], <<?[String], <<?[String], <<?[String]))
  }
  /** Table description of table harvest_object_extra. Objects of this class serve as prototypes for rows in queries. */
  class HarvestObjectExtra(tag: Tag) extends Table[HarvestObjectExtraRow](tag, "harvest_object_extra") {
    def * = (id, harvestObjectId, key, value) <> (HarvestObjectExtraRow.tupled, HarvestObjectExtraRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, harvestObjectId, key, value).shaped.<>({r=>import r._; _1.map(_=> HarvestObjectExtraRow.tupled((_1.get, _2, _3, _4)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id PrimaryKey */
    val id: Column[String] = column[String]("id", O.PrimaryKey)
    /** Database column harvest_object_id  */
    val harvestObjectId: Column[Option[String]] = column[Option[String]]("harvest_object_id")
    /** Database column key  */
    val key: Column[Option[String]] = column[Option[String]]("key")
    /** Database column value  */
    val value: Column[Option[String]] = column[Option[String]]("value")
    
    /** Foreign key referencing HarvestObject (database name harvest_object_extra_harvest_object_id_fkey) */
    val harvestObjectFk = foreignKey("harvest_object_extra_harvest_object_id_fkey", harvestObjectId, HarvestObject)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table HarvestObjectExtra */
  lazy val HarvestObjectExtra = new TableQuery(tag => new HarvestObjectExtra(tag))
  
  /** Entity class storing rows of table HarvestSource
   *  @param id Database column id PrimaryKey
   *  @param url Database column url 
   *  @param title Database column title 
   *  @param description Database column description 
   *  @param config Database column config 
   *  @param created Database column created 
   *  @param `type` Database column type 
   *  @param active Database column active 
   *  @param userId Database column user_id 
   *  @param publisherId Database column publisher_id 
   *  @param frequency Database column frequency 
   *  @param nextRun Database column next_run  */
  case class HarvestSourceRow(id: String, url: String, title: Option[String], description: Option[String], config: Option[String], created: Option[java.sql.Timestamp], `type`: String, active: Option[Boolean], userId: Option[String], publisherId: Option[String], frequency: Option[String], nextRun: Option[java.sql.Timestamp])
  /** GetResult implicit for fetching HarvestSourceRow objects using plain SQL queries */
  implicit def GetResultHarvestSourceRow(implicit e0: GR[String], e1: GR[java.sql.Timestamp], e2: GR[Boolean]): GR[HarvestSourceRow] = GR{
    prs => import prs._
    HarvestSourceRow.tupled((<<[String], <<[String], <<?[String], <<?[String], <<?[String], <<?[java.sql.Timestamp], <<[String], <<?[Boolean], <<?[String], <<?[String], <<?[String], <<?[java.sql.Timestamp]))
  }
  /** Table description of table harvest_source. Objects of this class serve as prototypes for rows in queries.
   *  NOTE: The following names collided with Scala keywords and were escaped: type */
  class HarvestSource(tag: Tag) extends Table[HarvestSourceRow](tag, "harvest_source") {
    def * = (id, url, title, description, config, created, `type`, active, userId, publisherId, frequency, nextRun) <> (HarvestSourceRow.tupled, HarvestSourceRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, url.?, title, description, config, created, `type`.?, active, userId, publisherId, frequency, nextRun).shaped.<>({r=>import r._; _1.map(_=> HarvestSourceRow.tupled((_1.get, _2.get, _3, _4, _5, _6, _7.get, _8, _9, _10, _11, _12)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id PrimaryKey */
    val id: Column[String] = column[String]("id", O.PrimaryKey)
    /** Database column url  */
    val url: Column[String] = column[String]("url")
    /** Database column title  */
    val title: Column[Option[String]] = column[Option[String]]("title")
    /** Database column description  */
    val description: Column[Option[String]] = column[Option[String]]("description")
    /** Database column config  */
    val config: Column[Option[String]] = column[Option[String]]("config")
    /** Database column created  */
    val created: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("created")
    /** Database column type 
     *  NOTE: The name was escaped because it collided with a Scala keyword. */
    val `type`: Column[String] = column[String]("type")
    /** Database column active  */
    val active: Column[Option[Boolean]] = column[Option[Boolean]]("active")
    /** Database column user_id  */
    val userId: Column[Option[String]] = column[Option[String]]("user_id")
    /** Database column publisher_id  */
    val publisherId: Column[Option[String]] = column[Option[String]]("publisher_id")
    /** Database column frequency  */
    val frequency: Column[Option[String]] = column[Option[String]]("frequency")
    /** Database column next_run  */
    val nextRun: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("next_run")
  }
  /** Collection-like TableQuery object for table HarvestSource */
  lazy val HarvestSource = new TableQuery(tag => new HarvestSource(tag))
  
  /** Entity class storing rows of table KombuMessage
   *  @param id Database column id PrimaryKey
   *  @param visible Database column visible 
   *  @param timestamp Database column timestamp 
   *  @param payload Database column payload 
   *  @param queueId Database column queue_id 
   *  @param version Database column version  */
  case class KombuMessageRow(id: Int, visible: Option[Boolean], timestamp: Option[java.sql.Timestamp], payload: String, queueId: Option[Int], version: Short)
  /** GetResult implicit for fetching KombuMessageRow objects using plain SQL queries */
  implicit def GetResultKombuMessageRow(implicit e0: GR[Boolean], e1: GR[Short], e2: GR[Int], e3: GR[java.sql.Timestamp], e4: GR[String]): GR[KombuMessageRow] = GR{
    prs => import prs._
    KombuMessageRow.tupled((<<[Int], <<?[Boolean], <<?[java.sql.Timestamp], <<[String], <<?[Int], <<[Short]))
  }
  /** Table description of table kombu_message. Objects of this class serve as prototypes for rows in queries. */
  class KombuMessage(tag: Tag) extends Table[KombuMessageRow](tag, "kombu_message") {
    def * = (id, visible, timestamp, payload, queueId, version) <> (KombuMessageRow.tupled, KombuMessageRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, visible, timestamp, payload.?, queueId, version.?).shaped.<>({r=>import r._; _1.map(_=> KombuMessageRow.tupled((_1.get, _2, _3, _4.get, _5, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id PrimaryKey */
    val id: Column[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column visible  */
    val visible: Column[Option[Boolean]] = column[Option[Boolean]]("visible")
    /** Database column timestamp  */
    val timestamp: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("timestamp")
    /** Database column payload  */
    val payload: Column[String] = column[String]("payload")
    /** Database column queue_id  */
    val queueId: Column[Option[Int]] = column[Option[Int]]("queue_id")
    /** Database column version  */
    val version: Column[Short] = column[Short]("version")
    
    /** Foreign key referencing KombuQueue (database name FK_kombu_message_queue) */
    val kombuQueueFk = foreignKey("FK_kombu_message_queue", queueId, KombuQueue)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    
    /** Index over (timestamp) (database name ix_kombu_message_timestamp) */
    val index1 = index("ix_kombu_message_timestamp", timestamp)
    /** Index over (visible) (database name ix_kombu_message_visible) */
    val index2 = index("ix_kombu_message_visible", visible)
  }
  /** Collection-like TableQuery object for table KombuMessage */
  lazy val KombuMessage = new TableQuery(tag => new KombuMessage(tag))
  
  /** Entity class storing rows of table KombuQueue
   *  @param id Database column id PrimaryKey
   *  @param name Database column name  */
  case class KombuQueueRow(id: Int, name: Option[String])
  /** GetResult implicit for fetching KombuQueueRow objects using plain SQL queries */
  implicit def GetResultKombuQueueRow(implicit e0: GR[Int], e1: GR[String]): GR[KombuQueueRow] = GR{
    prs => import prs._
    KombuQueueRow.tupled((<<[Int], <<?[String]))
  }
  /** Table description of table kombu_queue. Objects of this class serve as prototypes for rows in queries. */
  class KombuQueue(tag: Tag) extends Table[KombuQueueRow](tag, "kombu_queue") {
    def * = (id, name) <> (KombuQueueRow.tupled, KombuQueueRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, name).shaped.<>({r=>import r._; _1.map(_=> KombuQueueRow.tupled((_1.get, _2)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id PrimaryKey */
    val id: Column[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column name  */
    val name: Column[Option[String]] = column[Option[String]]("name")
    
    /** Uniqueness Index over (name) (database name kombu_queue_name_key) */
    val index1 = index("kombu_queue_name_key", name, unique=true)
  }
  /** Collection-like TableQuery object for table KombuQueue */
  lazy val KombuQueue = new TableQuery(tag => new KombuQueue(tag))
  
  /** Entity class storing rows of table Member
   *  @param id Database column id PrimaryKey
   *  @param tableId Database column table_id 
   *  @param groupId Database column group_id 
   *  @param state Database column state 
   *  @param revisionId Database column revision_id 
   *  @param tableNameX Database column table_name 
   *  @param capacity Database column capacity  */
  case class MemberRow(id: String, tableId: String, groupId: Option[String], state: Option[String], revisionId: Option[String], tableNameX: String, capacity: String)
  /** GetResult implicit for fetching MemberRow objects using plain SQL queries */
  implicit def GetResultMemberRow(implicit e0: GR[String]): GR[MemberRow] = GR{
    prs => import prs._
    MemberRow.tupled((<<[String], <<[String], <<?[String], <<?[String], <<?[String], <<[String], <<[String]))
  }
  /** Table description of table member. Objects of this class serve as prototypes for rows in queries.
   *  NOTE: The following names collided with Scala method names and were disambiguated: tableName */
  class Member(tag: Tag) extends Table[MemberRow](tag, "member") {
    def * = (id, tableId, groupId, state, revisionId, tableNameX, capacity) <> (MemberRow.tupled, MemberRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, tableId.?, groupId, state, revisionId, tableNameX.?, capacity.?).shaped.<>({r=>import r._; _1.map(_=> MemberRow.tupled((_1.get, _2.get, _3, _4, _5, _6.get, _7.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id PrimaryKey */
    val id: Column[String] = column[String]("id", O.PrimaryKey)
    /** Database column table_id  */
    val tableId: Column[String] = column[String]("table_id")
    /** Database column group_id  */
    val groupId: Column[Option[String]] = column[Option[String]]("group_id")
    /** Database column state  */
    val state: Column[Option[String]] = column[Option[String]]("state")
    /** Database column revision_id  */
    val revisionId: Column[Option[String]] = column[Option[String]]("revision_id")
    /** Database column table_name 
     *  NOTE: The name was disambiguated because it collided with Slick's method Table#tableName. */
    val tableNameX: Column[String] = column[String]("table_name")
    /** Database column capacity  */
    val capacity: Column[String] = column[String]("capacity")
    
    /** Foreign key referencing Group (database name member_group_id_fkey) */
    val groupFk = foreignKey("member_group_id_fkey", groupId, Group)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Revision (database name member_revision_id_fkey) */
    val revisionFk = foreignKey("member_revision_id_fkey", revisionId, Revision)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    
    /** Index over (groupId,tableId) (database name idx_extra_grp_id_pkg_id) */
    val index1 = index("idx_extra_grp_id_pkg_id", (groupId, tableId))
    /** Index over (tableId) (database name idx_group_pkg_id) */
    val index2 = index("idx_group_pkg_id", tableId)
    /** Index over (id) (database name idx_package_group_id) */
    val index3 = index("idx_package_group_id", id)
    /** Index over (tableId) (database name idx_package_group_pkg_id) */
    val index4 = index("idx_package_group_pkg_id", tableId)
    /** Index over (groupId,tableId) (database name idx_package_group_pkg_id_group_id) */
    val index5 = index("idx_package_group_pkg_id_group_id", (groupId, tableId))
  }
  /** Collection-like TableQuery object for table Member */
  lazy val Member = new TableQuery(tag => new Member(tag))
  
  /** Entity class storing rows of table MemberRevision
   *  @param id Database column id 
   *  @param tableId Database column table_id 
   *  @param groupId Database column group_id 
   *  @param state Database column state 
   *  @param revisionId Database column revision_id 
   *  @param continuityId Database column continuity_id 
   *  @param expiredId Database column expired_id 
   *  @param revisionTimestamp Database column revision_timestamp 
   *  @param expiredTimestamp Database column expired_timestamp 
   *  @param current Database column current 
   *  @param tableNameX Database column table_name 
   *  @param capacity Database column capacity  */
  case class MemberRevisionRow(id: String, tableId: String, groupId: Option[String], state: Option[String], revisionId: String, continuityId: Option[String], expiredId: Option[String], revisionTimestamp: Option[java.sql.Timestamp], expiredTimestamp: Option[java.sql.Timestamp], current: Option[Boolean], tableNameX: String, capacity: String)
  /** GetResult implicit for fetching MemberRevisionRow objects using plain SQL queries */
  implicit def GetResultMemberRevisionRow(implicit e0: GR[String], e1: GR[java.sql.Timestamp], e2: GR[Boolean]): GR[MemberRevisionRow] = GR{
    prs => import prs._
    MemberRevisionRow.tupled((<<[String], <<[String], <<?[String], <<?[String], <<[String], <<?[String], <<?[String], <<?[java.sql.Timestamp], <<?[java.sql.Timestamp], <<?[Boolean], <<[String], <<[String]))
  }
  /** Table description of table member_revision. Objects of this class serve as prototypes for rows in queries.
   *  NOTE: The following names collided with Scala method names and were disambiguated: tableName */
  class MemberRevision(tag: Tag) extends Table[MemberRevisionRow](tag, "member_revision") {
    def * = (id, tableId, groupId, state, revisionId, continuityId, expiredId, revisionTimestamp, expiredTimestamp, current, tableNameX, capacity) <> (MemberRevisionRow.tupled, MemberRevisionRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, tableId.?, groupId, state, revisionId.?, continuityId, expiredId, revisionTimestamp, expiredTimestamp, current, tableNameX.?, capacity.?).shaped.<>({r=>import r._; _1.map(_=> MemberRevisionRow.tupled((_1.get, _2.get, _3, _4, _5.get, _6, _7, _8, _9, _10, _11.get, _12.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id  */
    val id: Column[String] = column[String]("id")
    /** Database column table_id  */
    val tableId: Column[String] = column[String]("table_id")
    /** Database column group_id  */
    val groupId: Column[Option[String]] = column[Option[String]]("group_id")
    /** Database column state  */
    val state: Column[Option[String]] = column[Option[String]]("state")
    /** Database column revision_id  */
    val revisionId: Column[String] = column[String]("revision_id")
    /** Database column continuity_id  */
    val continuityId: Column[Option[String]] = column[Option[String]]("continuity_id")
    /** Database column expired_id  */
    val expiredId: Column[Option[String]] = column[Option[String]]("expired_id")
    /** Database column revision_timestamp  */
    val revisionTimestamp: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("revision_timestamp")
    /** Database column expired_timestamp  */
    val expiredTimestamp: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("expired_timestamp")
    /** Database column current  */
    val current: Column[Option[Boolean]] = column[Option[Boolean]]("current")
    /** Database column table_name 
     *  NOTE: The name was disambiguated because it collided with Slick's method Table#tableName. */
    val tableNameX: Column[String] = column[String]("table_name")
    /** Database column capacity  */
    val capacity: Column[String] = column[String]("capacity")
    
    /** Primary key of MemberRevision (database name member_revision_pkey) */
    val pk = primaryKey("member_revision_pkey", (id, revisionId))
    
    /** Foreign key referencing Group (database name member_revision_group_id_fkey) */
    val groupFk = foreignKey("member_revision_group_id_fkey", groupId, Group)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Member (database name member_revision_continuity_id_fkey) */
    val memberFk = foreignKey("member_revision_continuity_id_fkey", continuityId, Member)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Revision (database name member_revision_revision_id_fkey) */
    val revisionFk = foreignKey("member_revision_revision_id_fkey", revisionId, Revision)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    
    /** Index over (current) (database name idx_package_group_current) */
    val index1 = index("idx_package_group_current", current)
    /** Index over (revisionTimestamp,expiredTimestamp,tableId,groupId) (database name idx_package_group_period_package_group) */
    val index2 = index("idx_package_group_period_package_group", (revisionTimestamp, expiredTimestamp, tableId, groupId))
  }
  /** Collection-like TableQuery object for table MemberRevision */
  lazy val MemberRevision = new TableQuery(tag => new MemberRevision(tag))
  
  /** Entity class storing rows of table MigrateVersion
   *  @param repositoryId Database column repository_id PrimaryKey
   *  @param repositoryPath Database column repository_path 
   *  @param version Database column version  */
  case class MigrateVersionRow(repositoryId: String, repositoryPath: Option[String], version: Option[Int])
  /** GetResult implicit for fetching MigrateVersionRow objects using plain SQL queries */
  implicit def GetResultMigrateVersionRow(implicit e0: GR[String], e1: GR[Int]): GR[MigrateVersionRow] = GR{
    prs => import prs._
    MigrateVersionRow.tupled((<<[String], <<?[String], <<?[Int]))
  }
  /** Table description of table migrate_version. Objects of this class serve as prototypes for rows in queries. */
  class MigrateVersion(tag: Tag) extends Table[MigrateVersionRow](tag, "migrate_version") {
    def * = (repositoryId, repositoryPath, version) <> (MigrateVersionRow.tupled, MigrateVersionRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (repositoryId.?, repositoryPath, version).shaped.<>({r=>import r._; _1.map(_=> MigrateVersionRow.tupled((_1.get, _2, _3)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column repository_id PrimaryKey */
    val repositoryId: Column[String] = column[String]("repository_id", O.PrimaryKey)
    /** Database column repository_path  */
    val repositoryPath: Column[Option[String]] = column[Option[String]]("repository_path")
    /** Database column version  */
    val version: Column[Option[Int]] = column[Option[Int]]("version")
  }
  /** Collection-like TableQuery object for table MigrateVersion */
  lazy val MigrateVersion = new TableQuery(tag => new MigrateVersion(tag))
  
  /** Entity class storing rows of table Package
   *  @param id Database column id PrimaryKey
   *  @param name Database column name 
   *  @param title Database column title 
   *  @param version Database column version 
   *  @param url Database column url 
   *  @param notes Database column notes 
   *  @param licenseId Database column license_id 
   *  @param revisionId Database column revision_id 
   *  @param author Database column author 
   *  @param authorEmail Database column author_email 
   *  @param maintainer Database column maintainer 
   *  @param maintainerEmail Database column maintainer_email 
   *  @param state Database column state 
   *  @param `type` Database column type 
   *  @param ownerOrg Database column owner_org 
   *  @param `private` Database column private Default(false) */
  case class PackageRow(id: String, name: String, title: Option[String], version: Option[String], url: Option[String], notes: Option[String], licenseId: Option[String], revisionId: Option[String], author: Option[String], authorEmail: Option[String], maintainer: Option[String], maintainerEmail: Option[String], state: Option[String], `type`: Option[String], ownerOrg: Option[String], `private`: Option[Boolean]=false)
  /** GetResult implicit for fetching PackageRow objects using plain SQL queries */
  implicit def GetResultPackageRow(implicit e0: GR[String], e1: GR[Boolean]): GR[PackageRow] = GR{
    prs => import prs._
    PackageRow.tupled((<<[String], <<[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[Boolean]))
  }
  /** Table description of table package. Objects of this class serve as prototypes for rows in queries.
   *  NOTE: The following names collided with Scala keywords and were escaped: type, private */
  class Package(tag: Tag) extends Table[PackageRow](tag, "package") {
    def * = (id, name, title, version, url, notes, licenseId, revisionId, author, authorEmail, maintainer, maintainerEmail, state, `type`, ownerOrg, `private`) <> (PackageRow.tupled, PackageRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, name.?, title, version, url, notes, licenseId, revisionId, author, authorEmail, maintainer, maintainerEmail, state, `type`, ownerOrg, `private`).shaped.<>({r=>import r._; _1.map(_=> PackageRow.tupled((_1.get, _2.get, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id PrimaryKey */
    val id: Column[String] = column[String]("id", O.PrimaryKey)
    /** Database column name  */
    val name: Column[String] = column[String]("name")
    /** Database column title  */
    val title: Column[Option[String]] = column[Option[String]]("title")
    /** Database column version  */
    val version: Column[Option[String]] = column[Option[String]]("version")
    /** Database column url  */
    val url: Column[Option[String]] = column[Option[String]]("url")
    /** Database column notes  */
    val notes: Column[Option[String]] = column[Option[String]]("notes")
    /** Database column license_id  */
    val licenseId: Column[Option[String]] = column[Option[String]]("license_id")
    /** Database column revision_id  */
    val revisionId: Column[Option[String]] = column[Option[String]]("revision_id")
    /** Database column author  */
    val author: Column[Option[String]] = column[Option[String]]("author")
    /** Database column author_email  */
    val authorEmail: Column[Option[String]] = column[Option[String]]("author_email")
    /** Database column maintainer  */
    val maintainer: Column[Option[String]] = column[Option[String]]("maintainer")
    /** Database column maintainer_email  */
    val maintainerEmail: Column[Option[String]] = column[Option[String]]("maintainer_email")
    /** Database column state  */
    val state: Column[Option[String]] = column[Option[String]]("state")
    /** Database column type 
     *  NOTE: The name was escaped because it collided with a Scala keyword. */
    val `type`: Column[Option[String]] = column[Option[String]]("type")
    /** Database column owner_org  */
    val ownerOrg: Column[Option[String]] = column[Option[String]]("owner_org")
    /** Database column private Default(false)
     *  NOTE: The name was escaped because it collided with a Scala keyword. */
    val `private`: Column[Option[Boolean]] = column[Option[Boolean]]("private", O.Default(false))
    
    /** Foreign key referencing Revision (database name package_revision_id_fkey) */
    val revisionFk = foreignKey("package_revision_id_fkey", revisionId, Revision)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    
    /** Index over (id) (database name idx_pkg_id) */
    val index1 = index("idx_pkg_id", id)
    /** Index over (name) (database name idx_pkg_name) */
    val index2 = index("idx_pkg_name", name)
    /** Index over (id,state) (database name idx_pkg_sid) */
    val index3 = index("idx_pkg_sid", (id, state))
    /** Index over (name,state) (database name idx_pkg_sname) */
    val index4 = index("idx_pkg_sname", (name, state))
    /** Index over (revisionId,state) (database name idx_pkg_srev_id) */
    val index5 = index("idx_pkg_srev_id", (revisionId, state))
    /** Index over (title,state) (database name idx_pkg_stitle) */
    val index6 = index("idx_pkg_stitle", (title, state))
    /** Index over (title) (database name idx_pkg_title) */
    val index7 = index("idx_pkg_title", title)
    /** Uniqueness Index over (name) (database name package_name_key) */
    val index8 = index("package_name_key", name, unique=true)
  }
  /** Collection-like TableQuery object for table Package */
  lazy val Package = new TableQuery(tag => new Package(tag))
  
  /** Entity class storing rows of table PackageExtent
   *  @param packageId Database column package_id PrimaryKey
   *  @param theGeom Database column the_geom  */
  case class PackageExtentRow(packageId: String, theGeom: Option[String])
  /** GetResult implicit for fetching PackageExtentRow objects using plain SQL queries */
  implicit def GetResultPackageExtentRow(implicit e0: GR[String]): GR[PackageExtentRow] = GR{
    prs => import prs._
    PackageExtentRow.tupled((<<[String], <<?[String]))
  }
  /** Table description of table package_extent. Objects of this class serve as prototypes for rows in queries. */
  class PackageExtent(tag: Tag) extends Table[PackageExtentRow](tag, "package_extent") {
    def * = (packageId, theGeom) <> (PackageExtentRow.tupled, PackageExtentRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (packageId.?, theGeom).shaped.<>({r=>import r._; _1.map(_=> PackageExtentRow.tupled((_1.get, _2)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column package_id PrimaryKey */
    val packageId: Column[String] = column[String]("package_id", O.PrimaryKey)
    /** Database column the_geom  */
    val theGeom: Column[Option[String]] = column[Option[String]]("the_geom")
    
    /** Index over (theGeom) (database name idx_package_extent_the_geom) */
    val index1 = index("idx_package_extent_the_geom", theGeom)
  }
  /** Collection-like TableQuery object for table PackageExtent */
  lazy val PackageExtent = new TableQuery(tag => new PackageExtent(tag))
  
  /** Entity class storing rows of table PackageExtra
   *  @param id Database column id PrimaryKey
   *  @param packageId Database column package_id 
   *  @param key Database column key 
   *  @param value Database column value 
   *  @param revisionId Database column revision_id 
   *  @param state Database column state  */
  case class PackageExtraRow(id: String, packageId: Option[String], key: Option[String], value: Option[String], revisionId: Option[String], state: Option[String])
  /** GetResult implicit for fetching PackageExtraRow objects using plain SQL queries */
  implicit def GetResultPackageExtraRow(implicit e0: GR[String]): GR[PackageExtraRow] = GR{
    prs => import prs._
    PackageExtraRow.tupled((<<[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String]))
  }
  /** Table description of table package_extra. Objects of this class serve as prototypes for rows in queries. */
  class PackageExtra(tag: Tag) extends Table[PackageExtraRow](tag, "package_extra") {
    def * = (id, packageId, key, value, revisionId, state) <> (PackageExtraRow.tupled, PackageExtraRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, packageId, key, value, revisionId, state).shaped.<>({r=>import r._; _1.map(_=> PackageExtraRow.tupled((_1.get, _2, _3, _4, _5, _6)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id PrimaryKey */
    val id: Column[String] = column[String]("id", O.PrimaryKey)
    /** Database column package_id  */
    val packageId: Column[Option[String]] = column[Option[String]]("package_id")
    /** Database column key  */
    val key: Column[Option[String]] = column[Option[String]]("key")
    /** Database column value  */
    val value: Column[Option[String]] = column[Option[String]]("value")
    /** Database column revision_id  */
    val revisionId: Column[Option[String]] = column[Option[String]]("revision_id")
    /** Database column state  */
    val state: Column[Option[String]] = column[Option[String]]("state")
    
    /** Foreign key referencing Package (database name package_extra_package_id_fkey) */
    val packageFk = foreignKey("package_extra_package_id_fkey", packageId, Package)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Revision (database name package_extra_revision_id_fkey) */
    val revisionFk = foreignKey("package_extra_revision_id_fkey", revisionId, Revision)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    
    /** Index over (id,packageId) (database name idx_extra_id_pkg_id) */
    val index1 = index("idx_extra_id_pkg_id", (id, packageId))
  }
  /** Collection-like TableQuery object for table PackageExtra */
  lazy val PackageExtra = new TableQuery(tag => new PackageExtra(tag))
  
  /** Entity class storing rows of table PackageExtraRevision
   *  @param id Database column id 
   *  @param packageId Database column package_id 
   *  @param key Database column key 
   *  @param value Database column value 
   *  @param revisionId Database column revision_id 
   *  @param continuityId Database column continuity_id 
   *  @param state Database column state 
   *  @param expiredId Database column expired_id 
   *  @param revisionTimestamp Database column revision_timestamp 
   *  @param expiredTimestamp Database column expired_timestamp 
   *  @param current Database column current  */
  case class PackageExtraRevisionRow(id: String, packageId: Option[String], key: Option[String], value: Option[String], revisionId: String, continuityId: Option[String], state: Option[String], expiredId: Option[String], revisionTimestamp: Option[java.sql.Timestamp], expiredTimestamp: Option[java.sql.Timestamp], current: Option[Boolean])
  /** GetResult implicit for fetching PackageExtraRevisionRow objects using plain SQL queries */
  implicit def GetResultPackageExtraRevisionRow(implicit e0: GR[String], e1: GR[java.sql.Timestamp], e2: GR[Boolean]): GR[PackageExtraRevisionRow] = GR{
    prs => import prs._
    PackageExtraRevisionRow.tupled((<<[String], <<?[String], <<?[String], <<?[String], <<[String], <<?[String], <<?[String], <<?[String], <<?[java.sql.Timestamp], <<?[java.sql.Timestamp], <<?[Boolean]))
  }
  /** Table description of table package_extra_revision. Objects of this class serve as prototypes for rows in queries. */
  class PackageExtraRevision(tag: Tag) extends Table[PackageExtraRevisionRow](tag, "package_extra_revision") {
    def * = (id, packageId, key, value, revisionId, continuityId, state, expiredId, revisionTimestamp, expiredTimestamp, current) <> (PackageExtraRevisionRow.tupled, PackageExtraRevisionRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, packageId, key, value, revisionId.?, continuityId, state, expiredId, revisionTimestamp, expiredTimestamp, current).shaped.<>({r=>import r._; _1.map(_=> PackageExtraRevisionRow.tupled((_1.get, _2, _3, _4, _5.get, _6, _7, _8, _9, _10, _11)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id  */
    val id: Column[String] = column[String]("id")
    /** Database column package_id  */
    val packageId: Column[Option[String]] = column[Option[String]]("package_id")
    /** Database column key  */
    val key: Column[Option[String]] = column[Option[String]]("key")
    /** Database column value  */
    val value: Column[Option[String]] = column[Option[String]]("value")
    /** Database column revision_id  */
    val revisionId: Column[String] = column[String]("revision_id")
    /** Database column continuity_id  */
    val continuityId: Column[Option[String]] = column[Option[String]]("continuity_id")
    /** Database column state  */
    val state: Column[Option[String]] = column[Option[String]]("state")
    /** Database column expired_id  */
    val expiredId: Column[Option[String]] = column[Option[String]]("expired_id")
    /** Database column revision_timestamp  */
    val revisionTimestamp: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("revision_timestamp")
    /** Database column expired_timestamp  */
    val expiredTimestamp: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("expired_timestamp")
    /** Database column current  */
    val current: Column[Option[Boolean]] = column[Option[Boolean]]("current")
    
    /** Primary key of PackageExtraRevision (database name package_extra_revision_pkey) */
    val pk = primaryKey("package_extra_revision_pkey", (id, revisionId))
    
    /** Foreign key referencing Package (database name package_extra_revision_package_id_fkey) */
    val packageFk = foreignKey("package_extra_revision_package_id_fkey", packageId, Package)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing PackageExtra (database name package_extra_revision_continuity_id_fkey) */
    val packageExtraFk = foreignKey("package_extra_revision_continuity_id_fkey", continuityId, PackageExtra)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Revision (database name package_extra_revision_revision_id_fkey) */
    val revisionFk = foreignKey("package_extra_revision_revision_id_fkey", revisionId, Revision)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    
    /** Index over (current) (database name idx_package_extra_current) */
    val index1 = index("idx_package_extra_current", current)
    /** Index over (packageId,current) (database name idx_package_extra_package_id) */
    val index2 = index("idx_package_extra_package_id", (packageId, current))
    /** Index over (revisionTimestamp,expiredTimestamp,id) (database name idx_package_extra_period) */
    val index3 = index("idx_package_extra_period", (revisionTimestamp, expiredTimestamp, id))
    /** Index over (revisionTimestamp,expiredTimestamp,packageId) (database name idx_package_extra_period_package) */
    val index4 = index("idx_package_extra_period_package", (revisionTimestamp, expiredTimestamp, packageId))
  }
  /** Collection-like TableQuery object for table PackageExtraRevision */
  lazy val PackageExtraRevision = new TableQuery(tag => new PackageExtraRevision(tag))
  
  /** Entity class storing rows of table PackageRelationship
   *  @param id Database column id PrimaryKey
   *  @param subjectPackageId Database column subject_package_id 
   *  @param objectPackageId Database column object_package_id 
   *  @param `type` Database column type 
   *  @param comment Database column comment 
   *  @param revisionId Database column revision_id 
   *  @param state Database column state  */
  case class PackageRelationshipRow(id: String, subjectPackageId: Option[String], objectPackageId: Option[String], `type`: Option[String], comment: Option[String], revisionId: Option[String], state: Option[String])
  /** GetResult implicit for fetching PackageRelationshipRow objects using plain SQL queries */
  implicit def GetResultPackageRelationshipRow(implicit e0: GR[String]): GR[PackageRelationshipRow] = GR{
    prs => import prs._
    PackageRelationshipRow.tupled((<<[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String]))
  }
  /** Table description of table package_relationship. Objects of this class serve as prototypes for rows in queries.
   *  NOTE: The following names collided with Scala keywords and were escaped: type */
  class PackageRelationship(tag: Tag) extends Table[PackageRelationshipRow](tag, "package_relationship") {
    def * = (id, subjectPackageId, objectPackageId, `type`, comment, revisionId, state) <> (PackageRelationshipRow.tupled, PackageRelationshipRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, subjectPackageId, objectPackageId, `type`, comment, revisionId, state).shaped.<>({r=>import r._; _1.map(_=> PackageRelationshipRow.tupled((_1.get, _2, _3, _4, _5, _6, _7)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id PrimaryKey */
    val id: Column[String] = column[String]("id", O.PrimaryKey)
    /** Database column subject_package_id  */
    val subjectPackageId: Column[Option[String]] = column[Option[String]]("subject_package_id")
    /** Database column object_package_id  */
    val objectPackageId: Column[Option[String]] = column[Option[String]]("object_package_id")
    /** Database column type 
     *  NOTE: The name was escaped because it collided with a Scala keyword. */
    val `type`: Column[Option[String]] = column[Option[String]]("type")
    /** Database column comment  */
    val comment: Column[Option[String]] = column[Option[String]]("comment")
    /** Database column revision_id  */
    val revisionId: Column[Option[String]] = column[Option[String]]("revision_id")
    /** Database column state  */
    val state: Column[Option[String]] = column[Option[String]]("state")
    
    /** Foreign key referencing Package (database name package_relationship_object_package_id_fkey) */
    val packageFk1 = foreignKey("package_relationship_object_package_id_fkey", objectPackageId, Package)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Package (database name package_relationship_subject_package_id_fkey) */
    val packageFk2 = foreignKey("package_relationship_subject_package_id_fkey", subjectPackageId, Package)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Revision (database name package_relationship_revision_id_fkey) */
    val revisionFk = foreignKey("package_relationship_revision_id_fkey", revisionId, Revision)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table PackageRelationship */
  lazy val PackageRelationship = new TableQuery(tag => new PackageRelationship(tag))
  
  /** Entity class storing rows of table PackageRelationshipRevision
   *  @param id Database column id 
   *  @param subjectPackageId Database column subject_package_id 
   *  @param objectPackageId Database column object_package_id 
   *  @param `type` Database column type 
   *  @param comment Database column comment 
   *  @param revisionId Database column revision_id 
   *  @param continuityId Database column continuity_id 
   *  @param state Database column state 
   *  @param expiredId Database column expired_id 
   *  @param revisionTimestamp Database column revision_timestamp 
   *  @param expiredTimestamp Database column expired_timestamp 
   *  @param current Database column current  */
  case class PackageRelationshipRevisionRow(id: String, subjectPackageId: Option[String], objectPackageId: Option[String], `type`: Option[String], comment: Option[String], revisionId: String, continuityId: Option[String], state: Option[String], expiredId: Option[String], revisionTimestamp: Option[java.sql.Timestamp], expiredTimestamp: Option[java.sql.Timestamp], current: Option[Boolean])
  /** GetResult implicit for fetching PackageRelationshipRevisionRow objects using plain SQL queries */
  implicit def GetResultPackageRelationshipRevisionRow(implicit e0: GR[String], e1: GR[java.sql.Timestamp], e2: GR[Boolean]): GR[PackageRelationshipRevisionRow] = GR{
    prs => import prs._
    PackageRelationshipRevisionRow.tupled((<<[String], <<?[String], <<?[String], <<?[String], <<?[String], <<[String], <<?[String], <<?[String], <<?[String], <<?[java.sql.Timestamp], <<?[java.sql.Timestamp], <<?[Boolean]))
  }
  /** Table description of table package_relationship_revision. Objects of this class serve as prototypes for rows in queries.
   *  NOTE: The following names collided with Scala keywords and were escaped: type */
  class PackageRelationshipRevision(tag: Tag) extends Table[PackageRelationshipRevisionRow](tag, "package_relationship_revision") {
    def * = (id, subjectPackageId, objectPackageId, `type`, comment, revisionId, continuityId, state, expiredId, revisionTimestamp, expiredTimestamp, current) <> (PackageRelationshipRevisionRow.tupled, PackageRelationshipRevisionRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, subjectPackageId, objectPackageId, `type`, comment, revisionId.?, continuityId, state, expiredId, revisionTimestamp, expiredTimestamp, current).shaped.<>({r=>import r._; _1.map(_=> PackageRelationshipRevisionRow.tupled((_1.get, _2, _3, _4, _5, _6.get, _7, _8, _9, _10, _11, _12)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id  */
    val id: Column[String] = column[String]("id")
    /** Database column subject_package_id  */
    val subjectPackageId: Column[Option[String]] = column[Option[String]]("subject_package_id")
    /** Database column object_package_id  */
    val objectPackageId: Column[Option[String]] = column[Option[String]]("object_package_id")
    /** Database column type 
     *  NOTE: The name was escaped because it collided with a Scala keyword. */
    val `type`: Column[Option[String]] = column[Option[String]]("type")
    /** Database column comment  */
    val comment: Column[Option[String]] = column[Option[String]]("comment")
    /** Database column revision_id  */
    val revisionId: Column[String] = column[String]("revision_id")
    /** Database column continuity_id  */
    val continuityId: Column[Option[String]] = column[Option[String]]("continuity_id")
    /** Database column state  */
    val state: Column[Option[String]] = column[Option[String]]("state")
    /** Database column expired_id  */
    val expiredId: Column[Option[String]] = column[Option[String]]("expired_id")
    /** Database column revision_timestamp  */
    val revisionTimestamp: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("revision_timestamp")
    /** Database column expired_timestamp  */
    val expiredTimestamp: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("expired_timestamp")
    /** Database column current  */
    val current: Column[Option[Boolean]] = column[Option[Boolean]]("current")
    
    /** Primary key of PackageRelationshipRevision (database name package_relationship_revision_pkey) */
    val pk = primaryKey("package_relationship_revision_pkey", (id, revisionId))
    
    /** Foreign key referencing Package (database name package_relationship_revision_object_package_id_fkey) */
    val packageFk1 = foreignKey("package_relationship_revision_object_package_id_fkey", objectPackageId, Package)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Package (database name package_relationship_revision_subject_package_id_fkey) */
    val packageFk2 = foreignKey("package_relationship_revision_subject_package_id_fkey", subjectPackageId, Package)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing PackageRelationship (database name package_relationship_revision_continuity_id_fkey) */
    val packageRelationshipFk = foreignKey("package_relationship_revision_continuity_id_fkey", continuityId, PackageRelationship)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Revision (database name package_relationship_revision_revision_id_fkey) */
    val revisionFk = foreignKey("package_relationship_revision_revision_id_fkey", revisionId, Revision)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    
    /** Index over (current) (database name idx_package_relationship_current) */
    val index1 = index("idx_package_relationship_current", current)
    /** Index over (revisionTimestamp,expiredTimestamp,objectPackageId,subjectPackageId) (database name idx_period_package_relationship) */
    val index2 = index("idx_period_package_relationship", (revisionTimestamp, expiredTimestamp, objectPackageId, subjectPackageId))
  }
  /** Collection-like TableQuery object for table PackageRelationshipRevision */
  lazy val PackageRelationshipRevision = new TableQuery(tag => new PackageRelationshipRevision(tag))
  
  /** Entity class storing rows of table PackageRevision
   *  @param id Database column id 
   *  @param name Database column name 
   *  @param title Database column title 
   *  @param version Database column version 
   *  @param url Database column url 
   *  @param notes Database column notes 
   *  @param licenseId Database column license_id 
   *  @param revisionId Database column revision_id 
   *  @param continuityId Database column continuity_id 
   *  @param author Database column author 
   *  @param authorEmail Database column author_email 
   *  @param maintainer Database column maintainer 
   *  @param maintainerEmail Database column maintainer_email 
   *  @param state Database column state 
   *  @param expiredId Database column expired_id 
   *  @param revisionTimestamp Database column revision_timestamp 
   *  @param expiredTimestamp Database column expired_timestamp 
   *  @param current Database column current 
   *  @param `type` Database column type 
   *  @param ownerOrg Database column owner_org 
   *  @param `private` Database column private Default(false) */
  case class PackageRevisionRow(id: String, name: String, title: Option[String], version: Option[String], url: Option[String], notes: Option[String], licenseId: Option[String], revisionId: String, continuityId: Option[String], author: Option[String], authorEmail: Option[String], maintainer: Option[String], maintainerEmail: Option[String], state: Option[String], expiredId: Option[String], revisionTimestamp: Option[java.sql.Timestamp], expiredTimestamp: Option[java.sql.Timestamp], current: Option[Boolean], `type`: Option[String], ownerOrg: Option[String], `private`: Option[Boolean]=false)
  /** GetResult implicit for fetching PackageRevisionRow objects using plain SQL queries */
  implicit def GetResultPackageRevisionRow(implicit e0: GR[String], e1: GR[java.sql.Timestamp], e2: GR[Boolean]): GR[PackageRevisionRow] = GR{
    prs => import prs._
    PackageRevisionRow.tupled((<<[String], <<[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[java.sql.Timestamp], <<?[java.sql.Timestamp], <<?[Boolean], <<?[String], <<?[String], <<?[Boolean]))
  }
  /** Table description of table package_revision. Objects of this class serve as prototypes for rows in queries.
   *  NOTE: The following names collided with Scala keywords and were escaped: type, private */
  class PackageRevision(tag: Tag) extends Table[PackageRevisionRow](tag, "package_revision") {
    def * = (id, name, title, version, url, notes, licenseId, revisionId, continuityId, author, authorEmail, maintainer, maintainerEmail, state, expiredId, revisionTimestamp, expiredTimestamp, current, `type`, ownerOrg, `private`) <> (PackageRevisionRow.tupled, PackageRevisionRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, name.?, title, version, url, notes, licenseId, revisionId.?, continuityId, author, authorEmail, maintainer, maintainerEmail, state, expiredId, revisionTimestamp, expiredTimestamp, current, `type`, ownerOrg, `private`).shaped.<>({r=>import r._; _1.map(_=> PackageRevisionRow.tupled((_1.get, _2.get, _3, _4, _5, _6, _7, _8.get, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id  */
    val id: Column[String] = column[String]("id")
    /** Database column name  */
    val name: Column[String] = column[String]("name")
    /** Database column title  */
    val title: Column[Option[String]] = column[Option[String]]("title")
    /** Database column version  */
    val version: Column[Option[String]] = column[Option[String]]("version")
    /** Database column url  */
    val url: Column[Option[String]] = column[Option[String]]("url")
    /** Database column notes  */
    val notes: Column[Option[String]] = column[Option[String]]("notes")
    /** Database column license_id  */
    val licenseId: Column[Option[String]] = column[Option[String]]("license_id")
    /** Database column revision_id  */
    val revisionId: Column[String] = column[String]("revision_id")
    /** Database column continuity_id  */
    val continuityId: Column[Option[String]] = column[Option[String]]("continuity_id")
    /** Database column author  */
    val author: Column[Option[String]] = column[Option[String]]("author")
    /** Database column author_email  */
    val authorEmail: Column[Option[String]] = column[Option[String]]("author_email")
    /** Database column maintainer  */
    val maintainer: Column[Option[String]] = column[Option[String]]("maintainer")
    /** Database column maintainer_email  */
    val maintainerEmail: Column[Option[String]] = column[Option[String]]("maintainer_email")
    /** Database column state  */
    val state: Column[Option[String]] = column[Option[String]]("state")
    /** Database column expired_id  */
    val expiredId: Column[Option[String]] = column[Option[String]]("expired_id")
    /** Database column revision_timestamp  */
    val revisionTimestamp: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("revision_timestamp")
    /** Database column expired_timestamp  */
    val expiredTimestamp: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("expired_timestamp")
    /** Database column current  */
    val current: Column[Option[Boolean]] = column[Option[Boolean]]("current")
    /** Database column type 
     *  NOTE: The name was escaped because it collided with a Scala keyword. */
    val `type`: Column[Option[String]] = column[Option[String]]("type")
    /** Database column owner_org  */
    val ownerOrg: Column[Option[String]] = column[Option[String]]("owner_org")
    /** Database column private Default(false)
     *  NOTE: The name was escaped because it collided with a Scala keyword. */
    val `private`: Column[Option[Boolean]] = column[Option[Boolean]]("private", O.Default(false))
    
    /** Primary key of PackageRevision (database name package_revision_pkey) */
    val pk = primaryKey("package_revision_pkey", (id, revisionId))
    
    /** Foreign key referencing Package (database name package_revision_continuity_id_fkey) */
    val packageFk = foreignKey("package_revision_continuity_id_fkey", continuityId, Package)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Revision (database name package_revision_revision_id_fkey) */
    val revisionFk = foreignKey("package_revision_revision_id_fkey", revisionId, Revision)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    
    /** Index over (current) (database name idx_package_current) */
    val index1 = index("idx_package_current", current)
    /** Index over (revisionTimestamp,expiredTimestamp,id) (database name idx_package_period) */
    val index2 = index("idx_package_period", (revisionTimestamp, expiredTimestamp, id))
    /** Index over (id) (database name idx_pkg_revision_id) */
    val index3 = index("idx_pkg_revision_id", id)
    /** Index over (name) (database name idx_pkg_revision_name) */
    val index4 = index("idx_pkg_revision_name", name)
  }
  /** Collection-like TableQuery object for table PackageRevision */
  lazy val PackageRevision = new TableQuery(tag => new PackageRevision(tag))
  
  /** Entity class storing rows of table PackageRole
   *  @param userObjectRoleId Database column user_object_role_id PrimaryKey
   *  @param packageId Database column package_id  */
  case class PackageRoleRow(userObjectRoleId: String, packageId: Option[String])
  /** GetResult implicit for fetching PackageRoleRow objects using plain SQL queries */
  implicit def GetResultPackageRoleRow(implicit e0: GR[String]): GR[PackageRoleRow] = GR{
    prs => import prs._
    PackageRoleRow.tupled((<<[String], <<?[String]))
  }
  /** Table description of table package_role. Objects of this class serve as prototypes for rows in queries. */
  class PackageRole(tag: Tag) extends Table[PackageRoleRow](tag, "package_role") {
    def * = (userObjectRoleId, packageId) <> (PackageRoleRow.tupled, PackageRoleRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (userObjectRoleId.?, packageId).shaped.<>({r=>import r._; _1.map(_=> PackageRoleRow.tupled((_1.get, _2)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column user_object_role_id PrimaryKey */
    val userObjectRoleId: Column[String] = column[String]("user_object_role_id", O.PrimaryKey)
    /** Database column package_id  */
    val packageId: Column[Option[String]] = column[Option[String]]("package_id")
    
    /** Foreign key referencing Package (database name package_role_package_id_fkey) */
    val packageFk = foreignKey("package_role_package_id_fkey", packageId, Package)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing UserObjectRole (database name package_role_user_object_role_id_fkey) */
    val userObjectRoleFk = foreignKey("package_role_user_object_role_id_fkey", userObjectRoleId, UserObjectRole)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table PackageRole */
  lazy val PackageRole = new TableQuery(tag => new PackageRole(tag))
  
  /** Entity class storing rows of table PackageTag
   *  @param id Database column id PrimaryKey
   *  @param packageId Database column package_id 
   *  @param tagId Database column tag_id 
   *  @param revisionId Database column revision_id 
   *  @param state Database column state  */
  case class PackageTagRow(id: String, packageId: Option[String], tagId: Option[String], revisionId: Option[String], state: Option[String])
  /** GetResult implicit for fetching PackageTagRow objects using plain SQL queries */
  implicit def GetResultPackageTagRow(implicit e0: GR[String]): GR[PackageTagRow] = GR{
    prs => import prs._
    PackageTagRow.tupled((<<[String], <<?[String], <<?[String], <<?[String], <<?[String]))
  }
  /** Table description of table package_tag. Objects of this class serve as prototypes for rows in queries. */
  class PackageTag(tag: Tag) extends Table[PackageTagRow](tag, "package_tag") {
    def * = (id, packageId, tagId, revisionId, state) <> (PackageTagRow.tupled, PackageTagRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, packageId, tagId, revisionId, state).shaped.<>({r=>import r._; _1.map(_=> PackageTagRow.tupled((_1.get, _2, _3, _4, _5)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id PrimaryKey */
    val id: Column[String] = column[String]("id", O.PrimaryKey)
    /** Database column package_id  */
    val packageId: Column[Option[String]] = column[Option[String]]("package_id")
    /** Database column tag_id  */
    val tagId: Column[Option[String]] = column[Option[String]]("tag_id")
    /** Database column revision_id  */
    val revisionId: Column[Option[String]] = column[Option[String]]("revision_id")
    /** Database column state  */
    val state: Column[Option[String]] = column[Option[String]]("state")
    
    /** Foreign key referencing Package (database name package_tag_package_id_fkey) */
    val packageFk = foreignKey("package_tag_package_id_fkey", packageId, Package)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Revision (database name package_tag_revision_id_fkey) */
    val revisionFk = foreignKey("package_tag_revision_id_fkey", revisionId, Revision)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Tag (database name package_tag_tag_id_fkey) */
    val tagFk = foreignKey("package_tag_tag_id_fkey", tagId, Tag)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    
    /** Index over (id) (database name idx_package_tag_id) */
    val index1 = index("idx_package_tag_id", id)
    /** Index over (tagId,packageId) (database name idx_package_tag_pkg_id_tag_id) */
    val index2 = index("idx_package_tag_pkg_id_tag_id", (tagId, packageId))
  }
  /** Collection-like TableQuery object for table PackageTag */
  lazy val PackageTag = new TableQuery(tag => new PackageTag(tag))
  
  /** Entity class storing rows of table PackageTagRevision
   *  @param id Database column id 
   *  @param packageId Database column package_id 
   *  @param tagId Database column tag_id 
   *  @param revisionId Database column revision_id 
   *  @param continuityId Database column continuity_id 
   *  @param state Database column state 
   *  @param expiredId Database column expired_id 
   *  @param revisionTimestamp Database column revision_timestamp 
   *  @param expiredTimestamp Database column expired_timestamp 
   *  @param current Database column current  */
  case class PackageTagRevisionRow(id: String, packageId: Option[String], tagId: Option[String], revisionId: String, continuityId: Option[String], state: Option[String], expiredId: Option[String], revisionTimestamp: Option[java.sql.Timestamp], expiredTimestamp: Option[java.sql.Timestamp], current: Option[Boolean])
  /** GetResult implicit for fetching PackageTagRevisionRow objects using plain SQL queries */
  implicit def GetResultPackageTagRevisionRow(implicit e0: GR[String], e1: GR[java.sql.Timestamp], e2: GR[Boolean]): GR[PackageTagRevisionRow] = GR{
    prs => import prs._
    PackageTagRevisionRow.tupled((<<[String], <<?[String], <<?[String], <<[String], <<?[String], <<?[String], <<?[String], <<?[java.sql.Timestamp], <<?[java.sql.Timestamp], <<?[Boolean]))
  }
  /** Table description of table package_tag_revision. Objects of this class serve as prototypes for rows in queries. */
  class PackageTagRevision(tag: Tag) extends Table[PackageTagRevisionRow](tag, "package_tag_revision") {
    def * = (id, packageId, tagId, revisionId, continuityId, state, expiredId, revisionTimestamp, expiredTimestamp, current) <> (PackageTagRevisionRow.tupled, PackageTagRevisionRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, packageId, tagId, revisionId.?, continuityId, state, expiredId, revisionTimestamp, expiredTimestamp, current).shaped.<>({r=>import r._; _1.map(_=> PackageTagRevisionRow.tupled((_1.get, _2, _3, _4.get, _5, _6, _7, _8, _9, _10)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id  */
    val id: Column[String] = column[String]("id")
    /** Database column package_id  */
    val packageId: Column[Option[String]] = column[Option[String]]("package_id")
    /** Database column tag_id  */
    val tagId: Column[Option[String]] = column[Option[String]]("tag_id")
    /** Database column revision_id  */
    val revisionId: Column[String] = column[String]("revision_id")
    /** Database column continuity_id  */
    val continuityId: Column[Option[String]] = column[Option[String]]("continuity_id")
    /** Database column state  */
    val state: Column[Option[String]] = column[Option[String]]("state")
    /** Database column expired_id  */
    val expiredId: Column[Option[String]] = column[Option[String]]("expired_id")
    /** Database column revision_timestamp  */
    val revisionTimestamp: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("revision_timestamp")
    /** Database column expired_timestamp  */
    val expiredTimestamp: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("expired_timestamp")
    /** Database column current  */
    val current: Column[Option[Boolean]] = column[Option[Boolean]]("current")
    
    /** Primary key of PackageTagRevision (database name package_tag_revision_pkey) */
    val pk = primaryKey("package_tag_revision_pkey", (id, revisionId))
    
    /** Foreign key referencing Package (database name package_tag_revision_package_id_fkey) */
    val packageFk = foreignKey("package_tag_revision_package_id_fkey", packageId, Package)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing PackageTag (database name package_tag_revision_continuity_id_fkey) */
    val packageTagFk = foreignKey("package_tag_revision_continuity_id_fkey", continuityId, PackageTag)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Revision (database name package_tag_revision_revision_id_fkey) */
    val revisionFk = foreignKey("package_tag_revision_revision_id_fkey", revisionId, Revision)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Tag (database name package_tag_revision_tag_id_fkey) */
    val tagFk = foreignKey("package_tag_revision_tag_id_fkey", tagId, Tag)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    
    /** Index over (current) (database name idx_package_tag_current) */
    val index1 = index("idx_package_tag_current", current)
    /** Index over (id) (database name idx_package_tag_revision_id) */
    val index2 = index("idx_package_tag_revision_id", id)
    /** Index over (tagId,packageId) (database name idx_package_tag_revision_pkg_id_tag_id) */
    val index3 = index("idx_package_tag_revision_pkg_id_tag_id", (tagId, packageId))
    /** Index over (revisionTimestamp,expiredTimestamp,packageId,tagId) (database name idx_period_package_tag) */
    val index4 = index("idx_period_package_tag", (revisionTimestamp, expiredTimestamp, packageId, tagId))
  }
  /** Collection-like TableQuery object for table PackageTagRevision */
  lazy val PackageTagRevision = new TableQuery(tag => new PackageTagRevision(tag))
  
  /** Entity class storing rows of table Rating
   *  @param id Database column id PrimaryKey
   *  @param userId Database column user_id 
   *  @param userIpAddress Database column user_ip_address 
   *  @param packageId Database column package_id 
   *  @param rating Database column rating 
   *  @param created Database column created  */
  case class RatingRow(id: String, userId: Option[String], userIpAddress: Option[String], packageId: Option[String], rating: Option[Double], created: Option[java.sql.Timestamp])
  /** GetResult implicit for fetching RatingRow objects using plain SQL queries */
  implicit def GetResultRatingRow(implicit e0: GR[String], e1: GR[Double], e2: GR[java.sql.Timestamp]): GR[RatingRow] = GR{
    prs => import prs._
    RatingRow.tupled((<<[String], <<?[String], <<?[String], <<?[String], <<?[Double], <<?[java.sql.Timestamp]))
  }
  /** Table description of table rating. Objects of this class serve as prototypes for rows in queries. */
  class Rating(tag: Tag) extends Table[RatingRow](tag, "rating") {
    def * = (id, userId, userIpAddress, packageId, rating, created) <> (RatingRow.tupled, RatingRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, userId, userIpAddress, packageId, rating, created).shaped.<>({r=>import r._; _1.map(_=> RatingRow.tupled((_1.get, _2, _3, _4, _5, _6)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id PrimaryKey */
    val id: Column[String] = column[String]("id", O.PrimaryKey)
    /** Database column user_id  */
    val userId: Column[Option[String]] = column[Option[String]]("user_id")
    /** Database column user_ip_address  */
    val userIpAddress: Column[Option[String]] = column[Option[String]]("user_ip_address")
    /** Database column package_id  */
    val packageId: Column[Option[String]] = column[Option[String]]("package_id")
    /** Database column rating  */
    val rating: Column[Option[Double]] = column[Option[Double]]("rating")
    /** Database column created  */
    val created: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("created")
    
    /** Foreign key referencing Package (database name rating_package_id_fkey) */
    val packageFk = foreignKey("rating_package_id_fkey", packageId, Package)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing User (database name rating_user_id_fkey) */
    val userFk = foreignKey("rating_user_id_fkey", userId, User)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    
    /** Index over (id) (database name idx_rating_id) */
    val index1 = index("idx_rating_id", id)
  }
  /** Collection-like TableQuery object for table Rating */
  lazy val Rating = new TableQuery(tag => new Rating(tag))
  
  /** Entity class storing rows of table Related
   *  @param id Database column id PrimaryKey
   *  @param `type` Database column type 
   *  @param title Database column title 
   *  @param description Database column description 
   *  @param imageUrl Database column image_url 
   *  @param url Database column url 
   *  @param created Database column created 
   *  @param ownerId Database column owner_id 
   *  @param viewCount Database column view_count Default(0)
   *  @param featured Database column featured Default(0) */
  case class RelatedRow(id: String, `type`: String, title: Option[String], description: Option[String], imageUrl: Option[String], url: Option[String], created: Option[java.sql.Timestamp], ownerId: Option[String], viewCount: Int=0, featured: Int=0)
  /** GetResult implicit for fetching RelatedRow objects using plain SQL queries */
  implicit def GetResultRelatedRow(implicit e0: GR[String], e1: GR[java.sql.Timestamp], e2: GR[Int]): GR[RelatedRow] = GR{
    prs => import prs._
    RelatedRow.tupled((<<[String], <<[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[java.sql.Timestamp], <<?[String], <<[Int], <<[Int]))
  }
  /** Table description of table related. Objects of this class serve as prototypes for rows in queries.
   *  NOTE: The following names collided with Scala keywords and were escaped: type */
  class Related(tag: Tag) extends Table[RelatedRow](tag, "related") {
    def * = (id, `type`, title, description, imageUrl, url, created, ownerId, viewCount, featured) <> (RelatedRow.tupled, RelatedRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, `type`.?, title, description, imageUrl, url, created, ownerId, viewCount.?, featured.?).shaped.<>({r=>import r._; _1.map(_=> RelatedRow.tupled((_1.get, _2.get, _3, _4, _5, _6, _7, _8, _9.get, _10.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id PrimaryKey */
    val id: Column[String] = column[String]("id", O.PrimaryKey)
    /** Database column type 
     *  NOTE: The name was escaped because it collided with a Scala keyword. */
    val `type`: Column[String] = column[String]("type")
    /** Database column title  */
    val title: Column[Option[String]] = column[Option[String]]("title")
    /** Database column description  */
    val description: Column[Option[String]] = column[Option[String]]("description")
    /** Database column image_url  */
    val imageUrl: Column[Option[String]] = column[Option[String]]("image_url")
    /** Database column url  */
    val url: Column[Option[String]] = column[Option[String]]("url")
    /** Database column created  */
    val created: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("created")
    /** Database column owner_id  */
    val ownerId: Column[Option[String]] = column[Option[String]]("owner_id")
    /** Database column view_count Default(0) */
    val viewCount: Column[Int] = column[Int]("view_count", O.Default(0))
    /** Database column featured Default(0) */
    val featured: Column[Int] = column[Int]("featured", O.Default(0))
  }
  /** Collection-like TableQuery object for table Related */
  lazy val Related = new TableQuery(tag => new Related(tag))
  
  /** Entity class storing rows of table RelatedDataset
   *  @param id Database column id PrimaryKey
   *  @param datasetId Database column dataset_id 
   *  @param relatedId Database column related_id 
   *  @param status Database column status  */
  case class RelatedDatasetRow(id: String, datasetId: String, relatedId: String, status: Option[String])
  /** GetResult implicit for fetching RelatedDatasetRow objects using plain SQL queries */
  implicit def GetResultRelatedDatasetRow(implicit e0: GR[String]): GR[RelatedDatasetRow] = GR{
    prs => import prs._
    RelatedDatasetRow.tupled((<<[String], <<[String], <<[String], <<?[String]))
  }
  /** Table description of table related_dataset. Objects of this class serve as prototypes for rows in queries. */
  class RelatedDataset(tag: Tag) extends Table[RelatedDatasetRow](tag, "related_dataset") {
    def * = (id, datasetId, relatedId, status) <> (RelatedDatasetRow.tupled, RelatedDatasetRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, datasetId.?, relatedId.?, status).shaped.<>({r=>import r._; _1.map(_=> RelatedDatasetRow.tupled((_1.get, _2.get, _3.get, _4)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id PrimaryKey */
    val id: Column[String] = column[String]("id", O.PrimaryKey)
    /** Database column dataset_id  */
    val datasetId: Column[String] = column[String]("dataset_id")
    /** Database column related_id  */
    val relatedId: Column[String] = column[String]("related_id")
    /** Database column status  */
    val status: Column[Option[String]] = column[Option[String]]("status")
    
    /** Foreign key referencing Package (database name related_dataset_dataset_id_fkey) */
    val packageFk = foreignKey("related_dataset_dataset_id_fkey", datasetId, Package)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Related (database name related_dataset_related_id_fkey) */
    val relatedFk = foreignKey("related_dataset_related_id_fkey", relatedId, Related)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table RelatedDataset */
  lazy val RelatedDataset = new TableQuery(tag => new RelatedDataset(tag))
  
  /** Entity class storing rows of table Resource
   *  @param id Database column id PrimaryKey
   *  @param resourceGroupId Database column resource_group_id 
   *  @param url Database column url 
   *  @param format Database column format 
   *  @param description Database column description 
   *  @param position Database column position 
   *  @param revisionId Database column revision_id 
   *  @param hash Database column hash 
   *  @param state Database column state 
   *  @param extras Database column extras 
   *  @param name Database column name 
   *  @param resourceType Database column resource_type 
   *  @param mimetype Database column mimetype 
   *  @param mimetypeInner Database column mimetype_inner 
   *  @param size Database column size 
   *  @param lastModified Database column last_modified 
   *  @param cacheUrl Database column cache_url 
   *  @param cacheLastUpdated Database column cache_last_updated 
   *  @param webstoreUrl Database column webstore_url 
   *  @param webstoreLastUpdated Database column webstore_last_updated 
   *  @param created Database column created  */
  case class ResourceRow(id: String, resourceGroupId: Option[String], url: String, format: Option[String], description: Option[String], position: Option[Int], revisionId: Option[String], hash: Option[String], state: Option[String], extras: Option[String], name: Option[String], resourceType: Option[String], mimetype: Option[String], mimetypeInner: Option[String], size: Option[Long], lastModified: Option[java.sql.Timestamp], cacheUrl: Option[String], cacheLastUpdated: Option[java.sql.Timestamp], webstoreUrl: Option[String], webstoreLastUpdated: Option[java.sql.Timestamp], created: Option[java.sql.Timestamp])
  /** GetResult implicit for fetching ResourceRow objects using plain SQL queries */
  implicit def GetResultResourceRow(implicit e0: GR[String], e1: GR[Int], e2: GR[Long], e3: GR[java.sql.Timestamp]): GR[ResourceRow] = GR{
    prs => import prs._
    ResourceRow.tupled((<<[String], <<?[String], <<[String], <<?[String], <<?[String], <<?[Int], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[Long], <<?[java.sql.Timestamp], <<?[String], <<?[java.sql.Timestamp], <<?[String], <<?[java.sql.Timestamp], <<?[java.sql.Timestamp]))
  }
  /** Table description of table resource. Objects of this class serve as prototypes for rows in queries. */
  class Resource(tag: Tag) extends Table[ResourceRow](tag, "resource") {
    def * = (id, resourceGroupId, url, format, description, position, revisionId, hash, state, extras, name, resourceType, mimetype, mimetypeInner, size, lastModified, cacheUrl, cacheLastUpdated, webstoreUrl, webstoreLastUpdated, created) <> (ResourceRow.tupled, ResourceRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, resourceGroupId, url.?, format, description, position, revisionId, hash, state, extras, name, resourceType, mimetype, mimetypeInner, size, lastModified, cacheUrl, cacheLastUpdated, webstoreUrl, webstoreLastUpdated, created).shaped.<>({r=>import r._; _1.map(_=> ResourceRow.tupled((_1.get, _2, _3.get, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20, _21)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id PrimaryKey */
    val id: Column[String] = column[String]("id", O.PrimaryKey)
    /** Database column resource_group_id  */
    val resourceGroupId: Column[Option[String]] = column[Option[String]]("resource_group_id")
    /** Database column url  */
    val url: Column[String] = column[String]("url")
    /** Database column format  */
    val format: Column[Option[String]] = column[Option[String]]("format")
    /** Database column description  */
    val description: Column[Option[String]] = column[Option[String]]("description")
    /** Database column position  */
    val position: Column[Option[Int]] = column[Option[Int]]("position")
    /** Database column revision_id  */
    val revisionId: Column[Option[String]] = column[Option[String]]("revision_id")
    /** Database column hash  */
    val hash: Column[Option[String]] = column[Option[String]]("hash")
    /** Database column state  */
    val state: Column[Option[String]] = column[Option[String]]("state")
    /** Database column extras  */
    val extras: Column[Option[String]] = column[Option[String]]("extras")
    /** Database column name  */
    val name: Column[Option[String]] = column[Option[String]]("name")
    /** Database column resource_type  */
    val resourceType: Column[Option[String]] = column[Option[String]]("resource_type")
    /** Database column mimetype  */
    val mimetype: Column[Option[String]] = column[Option[String]]("mimetype")
    /** Database column mimetype_inner  */
    val mimetypeInner: Column[Option[String]] = column[Option[String]]("mimetype_inner")
    /** Database column size  */
    val size: Column[Option[Long]] = column[Option[Long]]("size")
    /** Database column last_modified  */
    val lastModified: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("last_modified")
    /** Database column cache_url  */
    val cacheUrl: Column[Option[String]] = column[Option[String]]("cache_url")
    /** Database column cache_last_updated  */
    val cacheLastUpdated: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("cache_last_updated")
    /** Database column webstore_url  */
    val webstoreUrl: Column[Option[String]] = column[Option[String]]("webstore_url")
    /** Database column webstore_last_updated  */
    val webstoreLastUpdated: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("webstore_last_updated")
    /** Database column created  */
    val created: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("created")
    
    /** Foreign key referencing ResourceGroup (database name resource_resource_group_id_fkey) */
    val resourceGroupFk = foreignKey("resource_resource_group_id_fkey", resourceGroupId, ResourceGroup)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Revision (database name resource_revision_id_fkey) */
    val revisionFk = foreignKey("resource_revision_id_fkey", revisionId, Revision)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    
    /** Index over (id) (database name idx_package_resource_id) */
    val index1 = index("idx_package_resource_id", id)
    /** Index over (resourceGroupId,id) (database name idx_package_resource_pkg_id_resource_id) */
    val index2 = index("idx_package_resource_pkg_id_resource_id", (resourceGroupId, id))
    /** Index over (url) (database name idx_package_resource_url) */
    val index3 = index("idx_package_resource_url", url)
  }
  /** Collection-like TableQuery object for table Resource */
  lazy val Resource = new TableQuery(tag => new Resource(tag))
  
  /** Entity class storing rows of table ResourceGroup
   *  @param id Database column id PrimaryKey
   *  @param packageId Database column package_id 
   *  @param label Database column label 
   *  @param sortOrder Database column sort_order 
   *  @param extras Database column extras 
   *  @param state Database column state 
   *  @param revisionId Database column revision_id  */
  case class ResourceGroupRow(id: String, packageId: Option[String], label: Option[String], sortOrder: Option[String], extras: Option[String], state: Option[String], revisionId: Option[String])
  /** GetResult implicit for fetching ResourceGroupRow objects using plain SQL queries */
  implicit def GetResultResourceGroupRow(implicit e0: GR[String]): GR[ResourceGroupRow] = GR{
    prs => import prs._
    ResourceGroupRow.tupled((<<[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String]))
  }
  /** Table description of table resource_group. Objects of this class serve as prototypes for rows in queries. */
  class ResourceGroup(tag: Tag) extends Table[ResourceGroupRow](tag, "resource_group") {
    def * = (id, packageId, label, sortOrder, extras, state, revisionId) <> (ResourceGroupRow.tupled, ResourceGroupRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, packageId, label, sortOrder, extras, state, revisionId).shaped.<>({r=>import r._; _1.map(_=> ResourceGroupRow.tupled((_1.get, _2, _3, _4, _5, _6, _7)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id PrimaryKey */
    val id: Column[String] = column[String]("id", O.PrimaryKey)
    /** Database column package_id  */
    val packageId: Column[Option[String]] = column[Option[String]]("package_id")
    /** Database column label  */
    val label: Column[Option[String]] = column[Option[String]]("label")
    /** Database column sort_order  */
    val sortOrder: Column[Option[String]] = column[Option[String]]("sort_order")
    /** Database column extras  */
    val extras: Column[Option[String]] = column[Option[String]]("extras")
    /** Database column state  */
    val state: Column[Option[String]] = column[Option[String]]("state")
    /** Database column revision_id  */
    val revisionId: Column[Option[String]] = column[Option[String]]("revision_id")
    
    /** Foreign key referencing Package (database name resource_group_package_id_fkey) */
    val packageFk = foreignKey("resource_group_package_id_fkey", packageId, Package)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Revision (database name resource_group_revision_id_fkey) */
    val revisionFk = foreignKey("resource_group_revision_id_fkey", revisionId, Revision)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table ResourceGroup */
  lazy val ResourceGroup = new TableQuery(tag => new ResourceGroup(tag))
  
  /** Entity class storing rows of table ResourceGroupRevision
   *  @param id Database column id 
   *  @param packageId Database column package_id 
   *  @param label Database column label 
   *  @param sortOrder Database column sort_order 
   *  @param extras Database column extras 
   *  @param state Database column state 
   *  @param revisionId Database column revision_id 
   *  @param continuityId Database column continuity_id 
   *  @param expiredId Database column expired_id 
   *  @param revisionTimestamp Database column revision_timestamp 
   *  @param expiredTimestamp Database column expired_timestamp 
   *  @param current Database column current  */
  case class ResourceGroupRevisionRow(id: String, packageId: Option[String], label: Option[String], sortOrder: Option[String], extras: Option[String], state: Option[String], revisionId: String, continuityId: Option[String], expiredId: Option[String], revisionTimestamp: Option[java.sql.Timestamp], expiredTimestamp: Option[java.sql.Timestamp], current: Option[Boolean])
  /** GetResult implicit for fetching ResourceGroupRevisionRow objects using plain SQL queries */
  implicit def GetResultResourceGroupRevisionRow(implicit e0: GR[String], e1: GR[java.sql.Timestamp], e2: GR[Boolean]): GR[ResourceGroupRevisionRow] = GR{
    prs => import prs._
    ResourceGroupRevisionRow.tupled((<<[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<[String], <<?[String], <<?[String], <<?[java.sql.Timestamp], <<?[java.sql.Timestamp], <<?[Boolean]))
  }
  /** Table description of table resource_group_revision. Objects of this class serve as prototypes for rows in queries. */
  class ResourceGroupRevision(tag: Tag) extends Table[ResourceGroupRevisionRow](tag, "resource_group_revision") {
    def * = (id, packageId, label, sortOrder, extras, state, revisionId, continuityId, expiredId, revisionTimestamp, expiredTimestamp, current) <> (ResourceGroupRevisionRow.tupled, ResourceGroupRevisionRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, packageId, label, sortOrder, extras, state, revisionId.?, continuityId, expiredId, revisionTimestamp, expiredTimestamp, current).shaped.<>({r=>import r._; _1.map(_=> ResourceGroupRevisionRow.tupled((_1.get, _2, _3, _4, _5, _6, _7.get, _8, _9, _10, _11, _12)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id  */
    val id: Column[String] = column[String]("id")
    /** Database column package_id  */
    val packageId: Column[Option[String]] = column[Option[String]]("package_id")
    /** Database column label  */
    val label: Column[Option[String]] = column[Option[String]]("label")
    /** Database column sort_order  */
    val sortOrder: Column[Option[String]] = column[Option[String]]("sort_order")
    /** Database column extras  */
    val extras: Column[Option[String]] = column[Option[String]]("extras")
    /** Database column state  */
    val state: Column[Option[String]] = column[Option[String]]("state")
    /** Database column revision_id  */
    val revisionId: Column[String] = column[String]("revision_id")
    /** Database column continuity_id  */
    val continuityId: Column[Option[String]] = column[Option[String]]("continuity_id")
    /** Database column expired_id  */
    val expiredId: Column[Option[String]] = column[Option[String]]("expired_id")
    /** Database column revision_timestamp  */
    val revisionTimestamp: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("revision_timestamp")
    /** Database column expired_timestamp  */
    val expiredTimestamp: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("expired_timestamp")
    /** Database column current  */
    val current: Column[Option[Boolean]] = column[Option[Boolean]]("current")
    
    /** Primary key of ResourceGroupRevision (database name resource_group_revision_pkey) */
    val pk = primaryKey("resource_group_revision_pkey", (id, revisionId))
    
    /** Foreign key referencing Package (database name resource_group_revision_package_id_fkey) */
    val packageFk = foreignKey("resource_group_revision_package_id_fkey", packageId, Package)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing ResourceGroup (database name resource_group_revision_continuity_id_fkey) */
    val resourceGroupFk = foreignKey("resource_group_revision_continuity_id_fkey", continuityId, ResourceGroup)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Revision (database name resource_group_revision_revision_id_fkey) */
    val revisionFk = foreignKey("resource_group_revision_revision_id_fkey", revisionId, Revision)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    
    /** Index over (current) (database name idx_resource_group_current) */
    val index1 = index("idx_resource_group_current", current)
    /** Index over (revisionTimestamp,expiredTimestamp,id) (database name idx_resource_group_period) */
    val index2 = index("idx_resource_group_period", (revisionTimestamp, expiredTimestamp, id))
    /** Index over (revisionTimestamp,expiredTimestamp,packageId) (database name idx_resource_group_period_package) */
    val index3 = index("idx_resource_group_period_package", (revisionTimestamp, expiredTimestamp, packageId))
  }
  /** Collection-like TableQuery object for table ResourceGroupRevision */
  lazy val ResourceGroupRevision = new TableQuery(tag => new ResourceGroupRevision(tag))
  
  /** Row type of table ResourceRevision */
  type ResourceRevisionRow = String :: Option[String] :: String :: Option[String] :: Option[String] :: Option[Int] :: String :: Option[String] :: Option[String] :: Option[String] :: Option[String] :: Option[String] :: Option[java.sql.Timestamp] :: Option[java.sql.Timestamp] :: Option[Boolean] :: Option[String] :: Option[String] :: Option[String] :: Option[String] :: Option[Long] :: Option[java.sql.Timestamp] :: Option[String] :: Option[java.sql.Timestamp] :: Option[String] :: Option[java.sql.Timestamp] :: Option[java.sql.Timestamp] :: HNil
  /** GetResult implicit for fetching ResourceRevisionRow objects using plain SQL queries */
  implicit def GetResultResourceRevisionRow(implicit e0: GR[Boolean], e1: GR[Long], e2: GR[Int], e3: GR[java.sql.Timestamp], e4: GR[String]): GR[ResourceRevisionRow] = GR{
    prs => import prs._
    <<[String] :: <<?[String] :: <<[String] :: <<?[String] :: <<?[String] :: <<?[Int] :: <<[String] :: <<?[String] :: <<?[String] :: <<?[String] :: <<?[String] :: <<?[String] :: <<?[java.sql.Timestamp] :: <<?[java.sql.Timestamp] :: <<?[Boolean] :: <<?[String] :: <<?[String] :: <<?[String] :: <<?[String] :: <<?[Long] :: <<?[java.sql.Timestamp] :: <<?[String] :: <<?[java.sql.Timestamp] :: <<?[String] :: <<?[java.sql.Timestamp] :: <<?[java.sql.Timestamp] :: HNil
  }
  /** Table description of table resource_revision. Objects of this class serve as prototypes for rows in queries. */
  class ResourceRevision(tag: Tag) extends Table[ResourceRevisionRow](tag, "resource_revision") {
    def * = id :: resourceGroupId :: url :: format :: description :: position :: revisionId :: continuityId :: hash :: state :: extras :: expiredId :: revisionTimestamp :: expiredTimestamp :: current :: name :: resourceType :: mimetype :: mimetypeInner :: size :: lastModified :: cacheUrl :: cacheLastUpdated :: webstoreUrl :: webstoreLastUpdated :: created :: HNil
    
    /** Database column id  */
    val id: Column[String] = column[String]("id")
    /** Database column resource_group_id  */
    val resourceGroupId: Column[Option[String]] = column[Option[String]]("resource_group_id")
    /** Database column url  */
    val url: Column[String] = column[String]("url")
    /** Database column format  */
    val format: Column[Option[String]] = column[Option[String]]("format")
    /** Database column description  */
    val description: Column[Option[String]] = column[Option[String]]("description")
    /** Database column position  */
    val position: Column[Option[Int]] = column[Option[Int]]("position")
    /** Database column revision_id  */
    val revisionId: Column[String] = column[String]("revision_id")
    /** Database column continuity_id  */
    val continuityId: Column[Option[String]] = column[Option[String]]("continuity_id")
    /** Database column hash  */
    val hash: Column[Option[String]] = column[Option[String]]("hash")
    /** Database column state  */
    val state: Column[Option[String]] = column[Option[String]]("state")
    /** Database column extras  */
    val extras: Column[Option[String]] = column[Option[String]]("extras")
    /** Database column expired_id  */
    val expiredId: Column[Option[String]] = column[Option[String]]("expired_id")
    /** Database column revision_timestamp  */
    val revisionTimestamp: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("revision_timestamp")
    /** Database column expired_timestamp  */
    val expiredTimestamp: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("expired_timestamp")
    /** Database column current  */
    val current: Column[Option[Boolean]] = column[Option[Boolean]]("current")
    /** Database column name  */
    val name: Column[Option[String]] = column[Option[String]]("name")
    /** Database column resource_type  */
    val resourceType: Column[Option[String]] = column[Option[String]]("resource_type")
    /** Database column mimetype  */
    val mimetype: Column[Option[String]] = column[Option[String]]("mimetype")
    /** Database column mimetype_inner  */
    val mimetypeInner: Column[Option[String]] = column[Option[String]]("mimetype_inner")
    /** Database column size  */
    val size: Column[Option[Long]] = column[Option[Long]]("size")
    /** Database column last_modified  */
    val lastModified: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("last_modified")
    /** Database column cache_url  */
    val cacheUrl: Column[Option[String]] = column[Option[String]]("cache_url")
    /** Database column cache_last_updated  */
    val cacheLastUpdated: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("cache_last_updated")
    /** Database column webstore_url  */
    val webstoreUrl: Column[Option[String]] = column[Option[String]]("webstore_url")
    /** Database column webstore_last_updated  */
    val webstoreLastUpdated: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("webstore_last_updated")
    /** Database column created  */
    val created: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("created")
    
    /** Primary key of ResourceRevision (database name resource_revision_pkey) */
    val pk = primaryKey("resource_revision_pkey", id :: revisionId :: HNil)
    
    /** Foreign key referencing Resource (database name resource_revision_continuity_id_fkey) */
    val resourceFk = foreignKey("resource_revision_continuity_id_fkey", continuityId :: HNil, Resource)(r => r.id :: HNil, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing ResourceGroup (database name resource_revision_resource_group_id_fkey) */
    val resourceGroupFk = foreignKey("resource_revision_resource_group_id_fkey", resourceGroupId :: HNil, ResourceGroup)(r => r.id :: HNil, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Revision (database name resource_revision_revision_id_fkey) */
    val revisionFk = foreignKey("resource_revision_revision_id_fkey", revisionId :: HNil, Revision)(r => r.id :: HNil, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    
    /** Index over (current) (database name idx_resource_current) */
    val index1 = index("idx_resource_current", current :: HNil)
    /** Index over (revisionTimestamp,expiredTimestamp,id) (database name idx_resource_period) */
    val index2 = index("idx_resource_period", revisionTimestamp :: expiredTimestamp :: id :: HNil)
    /** Index over (revisionTimestamp,expiredTimestamp,resourceGroupId) (database name idx_resource_period_resource_group) */
    val index3 = index("idx_resource_period_resource_group", revisionTimestamp :: expiredTimestamp :: resourceGroupId :: HNil)
  }
  /** Collection-like TableQuery object for table ResourceRevision */
  lazy val ResourceRevision = new TableQuery(tag => new ResourceRevision(tag))
  
  /** Entity class storing rows of table Revision
   *  @param id Database column id PrimaryKey
   *  @param timestamp Database column timestamp 
   *  @param author Database column author 
   *  @param message Database column message 
   *  @param state Database column state 
   *  @param approvedTimestamp Database column approved_timestamp  */
  case class RevisionRow(id: String, timestamp: Option[java.sql.Timestamp], author: Option[String], message: Option[String], state: Option[String], approvedTimestamp: Option[java.sql.Timestamp])
  /** GetResult implicit for fetching RevisionRow objects using plain SQL queries */
  implicit def GetResultRevisionRow(implicit e0: GR[String], e1: GR[java.sql.Timestamp]): GR[RevisionRow] = GR{
    prs => import prs._
    RevisionRow.tupled((<<[String], <<?[java.sql.Timestamp], <<?[String], <<?[String], <<?[String], <<?[java.sql.Timestamp]))
  }
  /** Table description of table revision. Objects of this class serve as prototypes for rows in queries. */
  class Revision(tag: Tag) extends Table[RevisionRow](tag, "revision") {
    def * = (id, timestamp, author, message, state, approvedTimestamp) <> (RevisionRow.tupled, RevisionRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, timestamp, author, message, state, approvedTimestamp).shaped.<>({r=>import r._; _1.map(_=> RevisionRow.tupled((_1.get, _2, _3, _4, _5, _6)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id PrimaryKey */
    val id: Column[String] = column[String]("id", O.PrimaryKey)
    /** Database column timestamp  */
    val timestamp: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("timestamp")
    /** Database column author  */
    val author: Column[Option[String]] = column[Option[String]]("author")
    /** Database column message  */
    val message: Column[Option[String]] = column[Option[String]]("message")
    /** Database column state  */
    val state: Column[Option[String]] = column[Option[String]]("state")
    /** Database column approved_timestamp  */
    val approvedTimestamp: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("approved_timestamp")
    
    /** Index over (state) (database name idx_rev_state) */
    val index1 = index("idx_rev_state", state)
    /** Index over (author) (database name idx_revision_author) */
    val index2 = index("idx_revision_author", author)
  }
  /** Collection-like TableQuery object for table Revision */
  lazy val Revision = new TableQuery(tag => new Revision(tag))
  
  /** Entity class storing rows of table RoleAction
   *  @param id Database column id PrimaryKey
   *  @param role Database column role 
   *  @param context Database column context 
   *  @param action Database column action  */
  case class RoleActionRow(id: String, role: Option[String], context: String, action: Option[String])
  /** GetResult implicit for fetching RoleActionRow objects using plain SQL queries */
  implicit def GetResultRoleActionRow(implicit e0: GR[String]): GR[RoleActionRow] = GR{
    prs => import prs._
    RoleActionRow.tupled((<<[String], <<?[String], <<[String], <<?[String]))
  }
  /** Table description of table role_action. Objects of this class serve as prototypes for rows in queries. */
  class RoleAction(tag: Tag) extends Table[RoleActionRow](tag, "role_action") {
    def * = (id, role, context, action) <> (RoleActionRow.tupled, RoleActionRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, role, context.?, action).shaped.<>({r=>import r._; _1.map(_=> RoleActionRow.tupled((_1.get, _2, _3.get, _4)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id PrimaryKey */
    val id: Column[String] = column[String]("id", O.PrimaryKey)
    /** Database column role  */
    val role: Column[Option[String]] = column[Option[String]]("role")
    /** Database column context  */
    val context: Column[String] = column[String]("context")
    /** Database column action  */
    val action: Column[Option[String]] = column[Option[String]]("action")
    
    /** Index over (action) (database name idx_ra_action) */
    val index1 = index("idx_ra_action", action)
    /** Index over (role) (database name idx_ra_role) */
    val index2 = index("idx_ra_role", role)
    /** Index over (action,role) (database name idx_ra_role_action) */
    val index3 = index("idx_ra_role_action", (action, role))
  }
  /** Collection-like TableQuery object for table RoleAction */
  lazy val RoleAction = new TableQuery(tag => new RoleAction(tag))
  
  /** Entity class storing rows of table SpatialRefSys
   *  @param srid Database column srid PrimaryKey
   *  @param authName Database column auth_name 
   *  @param authSrid Database column auth_srid 
   *  @param srtext Database column srtext 
   *  @param proj4text Database column proj4text  */
  case class SpatialRefSysRow(srid: Int, authName: Option[String], authSrid: Option[Int], srtext: Option[String], proj4text: Option[String])
  /** GetResult implicit for fetching SpatialRefSysRow objects using plain SQL queries */
  implicit def GetResultSpatialRefSysRow(implicit e0: GR[Int], e1: GR[String]): GR[SpatialRefSysRow] = GR{
    prs => import prs._
    SpatialRefSysRow.tupled((<<[Int], <<?[String], <<?[Int], <<?[String], <<?[String]))
  }
  /** Table description of table spatial_ref_sys. Objects of this class serve as prototypes for rows in queries. */
  class SpatialRefSys(tag: Tag) extends Table[SpatialRefSysRow](tag, "spatial_ref_sys") {
    def * = (srid, authName, authSrid, srtext, proj4text) <> (SpatialRefSysRow.tupled, SpatialRefSysRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (srid.?, authName, authSrid, srtext, proj4text).shaped.<>({r=>import r._; _1.map(_=> SpatialRefSysRow.tupled((_1.get, _2, _3, _4, _5)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column srid PrimaryKey */
    val srid: Column[Int] = column[Int]("srid", O.PrimaryKey)
    /** Database column auth_name  */
    val authName: Column[Option[String]] = column[Option[String]]("auth_name")
    /** Database column auth_srid  */
    val authSrid: Column[Option[Int]] = column[Option[Int]]("auth_srid")
    /** Database column srtext  */
    val srtext: Column[Option[String]] = column[Option[String]]("srtext")
    /** Database column proj4text  */
    val proj4text: Column[Option[String]] = column[Option[String]]("proj4text")
  }
  /** Collection-like TableQuery object for table SpatialRefSys */
  lazy val SpatialRefSys = new TableQuery(tag => new SpatialRefSys(tag))
  
  /** Entity class storing rows of table SystemInfo
   *  @param id Database column id AutoInc, PrimaryKey
   *  @param key Database column key 
   *  @param value Database column value 
   *  @param revisionId Database column revision_id  */
  case class SystemInfoRow(id: Int, key: String, value: Option[String], revisionId: Option[String])
  /** GetResult implicit for fetching SystemInfoRow objects using plain SQL queries */
  implicit def GetResultSystemInfoRow(implicit e0: GR[Int], e1: GR[String]): GR[SystemInfoRow] = GR{
    prs => import prs._
    SystemInfoRow.tupled((<<[Int], <<[String], <<?[String], <<?[String]))
  }
  /** Table description of table system_info. Objects of this class serve as prototypes for rows in queries. */
  class SystemInfo(tag: Tag) extends Table[SystemInfoRow](tag, "system_info") {
    def * = (id, key, value, revisionId) <> (SystemInfoRow.tupled, SystemInfoRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, key.?, value, revisionId).shaped.<>({r=>import r._; _1.map(_=> SystemInfoRow.tupled((_1.get, _2.get, _3, _4)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id AutoInc, PrimaryKey */
    val id: Column[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column key  */
    val key: Column[String] = column[String]("key")
    /** Database column value  */
    val value: Column[Option[String]] = column[Option[String]]("value")
    /** Database column revision_id  */
    val revisionId: Column[Option[String]] = column[Option[String]]("revision_id")
    
    /** Foreign key referencing Revision (database name system_info_revision_id_fkey) */
    val revisionFk = foreignKey("system_info_revision_id_fkey", revisionId, Revision)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    
    /** Uniqueness Index over (key) (database name system_info_key_key) */
    val index1 = index("system_info_key_key", key, unique=true)
  }
  /** Collection-like TableQuery object for table SystemInfo */
  lazy val SystemInfo = new TableQuery(tag => new SystemInfo(tag))
  
  /** Entity class storing rows of table SystemInfoRevision
   *  @param id Database column id AutoInc
   *  @param key Database column key 
   *  @param value Database column value 
   *  @param revisionId Database column revision_id 
   *  @param continuityId Database column continuity_id  */
  case class SystemInfoRevisionRow(id: Int, key: String, value: Option[String], revisionId: String, continuityId: Option[Int])
  /** GetResult implicit for fetching SystemInfoRevisionRow objects using plain SQL queries */
  implicit def GetResultSystemInfoRevisionRow(implicit e0: GR[Int], e1: GR[String]): GR[SystemInfoRevisionRow] = GR{
    prs => import prs._
    SystemInfoRevisionRow.tupled((<<[Int], <<[String], <<?[String], <<[String], <<?[Int]))
  }
  /** Table description of table system_info_revision. Objects of this class serve as prototypes for rows in queries. */
  class SystemInfoRevision(tag: Tag) extends Table[SystemInfoRevisionRow](tag, "system_info_revision") {
    def * = (id, key, value, revisionId, continuityId) <> (SystemInfoRevisionRow.tupled, SystemInfoRevisionRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, key.?, value, revisionId.?, continuityId).shaped.<>({r=>import r._; _1.map(_=> SystemInfoRevisionRow.tupled((_1.get, _2.get, _3, _4.get, _5)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id AutoInc */
    val id: Column[Int] = column[Int]("id", O.AutoInc)
    /** Database column key  */
    val key: Column[String] = column[String]("key")
    /** Database column value  */
    val value: Column[Option[String]] = column[Option[String]]("value")
    /** Database column revision_id  */
    val revisionId: Column[String] = column[String]("revision_id")
    /** Database column continuity_id  */
    val continuityId: Column[Option[Int]] = column[Option[Int]]("continuity_id")
    
    /** Primary key of SystemInfoRevision (database name system_info_revision_pkey) */
    val pk = primaryKey("system_info_revision_pkey", (id, revisionId))
    
    /** Foreign key referencing Revision (database name system_info_revision_revision_id_fkey) */
    val revisionFk = foreignKey("system_info_revision_revision_id_fkey", revisionId, Revision)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing SystemInfo (database name system_info_revision_continuity_id_fkey) */
    val systemInfoFk = foreignKey("system_info_revision_continuity_id_fkey", continuityId, SystemInfo)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    
    /** Uniqueness Index over (key) (database name system_info_revision_key_key) */
    val index1 = index("system_info_revision_key_key", key, unique=true)
  }
  /** Collection-like TableQuery object for table SystemInfoRevision */
  lazy val SystemInfoRevision = new TableQuery(tag => new SystemInfoRevision(tag))
  
  /** Entity class storing rows of table SystemRole
   *  @param userObjectRoleId Database column user_object_role_id PrimaryKey */
  case class SystemRoleRow(userObjectRoleId: String)
  /** GetResult implicit for fetching SystemRoleRow objects using plain SQL queries */
  implicit def GetResultSystemRoleRow(implicit e0: GR[String]): GR[SystemRoleRow] = GR{
    prs => import prs._
    SystemRoleRow(<<[String])
  }
  /** Table description of table system_role. Objects of this class serve as prototypes for rows in queries. */
  class SystemRole(tag: Tag) extends Table[SystemRoleRow](tag, "system_role") {
    def * = userObjectRoleId <> (SystemRoleRow, SystemRoleRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = userObjectRoleId.?.shaped.<>({r=>import r._; _1.map(_=> SystemRoleRow(_1.get))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column user_object_role_id PrimaryKey */
    val userObjectRoleId: Column[String] = column[String]("user_object_role_id", O.PrimaryKey)
    
    /** Foreign key referencing UserObjectRole (database name system_role_user_object_role_id_fkey) */
    val userObjectRoleFk = foreignKey("system_role_user_object_role_id_fkey", userObjectRoleId, UserObjectRole)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table SystemRole */
  lazy val SystemRole = new TableQuery(tag => new SystemRole(tag))
  
  /** Entity class storing rows of table Tag
   *  @param id Database column id PrimaryKey
   *  @param name Database column name 
   *  @param vocabularyId Database column vocabulary_id  */
  case class TagRow(id: String, name: String, vocabularyId: Option[String])
  /** GetResult implicit for fetching TagRow objects using plain SQL queries */
  implicit def GetResultTagRow(implicit e0: GR[String]): GR[TagRow] = GR{
    prs => import prs._
    TagRow.tupled((<<[String], <<[String], <<?[String]))
  }
  /** Table description of table tag. Objects of this class serve as prototypes for rows in queries. */
  class Tag(tag: Tag) extends Table[TagRow](tag, "tag") {
    def * = (id, name, vocabularyId) <> (TagRow.tupled, TagRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, name.?, vocabularyId).shaped.<>({r=>import r._; _1.map(_=> TagRow.tupled((_1.get, _2.get, _3)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id PrimaryKey */
    val id: Column[String] = column[String]("id", O.PrimaryKey)
    /** Database column name  */
    val name: Column[String] = column[String]("name")
    /** Database column vocabulary_id  */
    val vocabularyId: Column[Option[String]] = column[Option[String]]("vocabulary_id")
    
    /** Foreign key referencing Vocabulary (database name tag_vocabulary_id_fkey) */
    val vocabularyFk = foreignKey("tag_vocabulary_id_fkey", vocabularyId, Vocabulary)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    
    /** Index over (id) (database name idx_tag_id) */
    val index1 = index("idx_tag_id", id)
    /** Index over (name) (database name idx_tag_name) */
    val index2 = index("idx_tag_name", name)
    /** Uniqueness Index over (name,vocabularyId) (database name tag_name_vocabulary_id_key) */
    val index3 = index("tag_name_vocabulary_id_key", (name, vocabularyId), unique=true)
  }
  /** Collection-like TableQuery object for table Tag */
  lazy val Tag = new TableQuery(tag => new Tag(tag))
  
  /** Entity class storing rows of table TaskStatus
   *  @param id Database column id PrimaryKey
   *  @param entityId Database column entity_id 
   *  @param entityType Database column entity_type 
   *  @param taskType Database column task_type 
   *  @param key Database column key 
   *  @param value Database column value 
   *  @param state Database column state 
   *  @param error Database column error 
   *  @param lastUpdated Database column last_updated  */
  case class TaskStatusRow(id: String, entityId: String, entityType: String, taskType: String, key: String, value: String, state: Option[String], error: Option[String], lastUpdated: Option[java.sql.Timestamp])
  /** GetResult implicit for fetching TaskStatusRow objects using plain SQL queries */
  implicit def GetResultTaskStatusRow(implicit e0: GR[String], e1: GR[java.sql.Timestamp]): GR[TaskStatusRow] = GR{
    prs => import prs._
    TaskStatusRow.tupled((<<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<?[String], <<?[String], <<?[java.sql.Timestamp]))
  }
  /** Table description of table task_status. Objects of this class serve as prototypes for rows in queries. */
  class TaskStatus(tag: Tag) extends Table[TaskStatusRow](tag, "task_status") {
    def * = (id, entityId, entityType, taskType, key, value, state, error, lastUpdated) <> (TaskStatusRow.tupled, TaskStatusRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, entityId.?, entityType.?, taskType.?, key.?, value.?, state, error, lastUpdated).shaped.<>({r=>import r._; _1.map(_=> TaskStatusRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7, _8, _9)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id PrimaryKey */
    val id: Column[String] = column[String]("id", O.PrimaryKey)
    /** Database column entity_id  */
    val entityId: Column[String] = column[String]("entity_id")
    /** Database column entity_type  */
    val entityType: Column[String] = column[String]("entity_type")
    /** Database column task_type  */
    val taskType: Column[String] = column[String]("task_type")
    /** Database column key  */
    val key: Column[String] = column[String]("key")
    /** Database column value  */
    val value: Column[String] = column[String]("value")
    /** Database column state  */
    val state: Column[Option[String]] = column[Option[String]]("state")
    /** Database column error  */
    val error: Column[Option[String]] = column[Option[String]]("error")
    /** Database column last_updated  */
    val lastUpdated: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("last_updated")
    
    /** Uniqueness Index over (entityId,taskType,key) (database name task_status_entity_id_task_type_key_key) */
    val index1 = index("task_status_entity_id_task_type_key_key", (entityId, taskType, key), unique=true)
  }
  /** Collection-like TableQuery object for table TaskStatus */
  lazy val TaskStatus = new TableQuery(tag => new TaskStatus(tag))
  
  /** Entity class storing rows of table TermTranslation
   *  @param term Database column term 
   *  @param termTranslation Database column term_translation 
   *  @param langCode Database column lang_code  */
  case class TermTranslationRow(term: String, termTranslation: String, langCode: String)
  /** GetResult implicit for fetching TermTranslationRow objects using plain SQL queries */
  implicit def GetResultTermTranslationRow(implicit e0: GR[String]): GR[TermTranslationRow] = GR{
    prs => import prs._
    TermTranslationRow.tupled((<<[String], <<[String], <<[String]))
  }
  /** Table description of table term_translation. Objects of this class serve as prototypes for rows in queries. */
  class TermTranslation(tag: Tag) extends Table[TermTranslationRow](tag, "term_translation") {
    def * = (term, termTranslation, langCode) <> (TermTranslationRow.tupled, TermTranslationRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (term.?, termTranslation.?, langCode.?).shaped.<>({r=>import r._; _1.map(_=> TermTranslationRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column term  */
    val term: Column[String] = column[String]("term")
    /** Database column term_translation  */
    val termTranslation: Column[String] = column[String]("term_translation")
    /** Database column lang_code  */
    val langCode: Column[String] = column[String]("lang_code")
    
    /** Index over (term) (database name term) */
    val index1 = index("term", term)
    /** Index over (term,langCode) (database name term_lang) */
    val index2 = index("term_lang", (term, langCode))
  }
  /** Collection-like TableQuery object for table TermTranslation */
  lazy val TermTranslation = new TableQuery(tag => new TermTranslation(tag))
  
  /** Entity class storing rows of table TrackingRaw
   *  @param userKey Database column user_key 
   *  @param url Database column url 
   *  @param trackingType Database column tracking_type 
   *  @param accessTimestamp Database column access_timestamp  */
  case class TrackingRawRow(userKey: String, url: String, trackingType: String, accessTimestamp: Option[java.sql.Timestamp])
  /** GetResult implicit for fetching TrackingRawRow objects using plain SQL queries */
  implicit def GetResultTrackingRawRow(implicit e0: GR[String], e1: GR[java.sql.Timestamp]): GR[TrackingRawRow] = GR{
    prs => import prs._
    TrackingRawRow.tupled((<<[String], <<[String], <<[String], <<?[java.sql.Timestamp]))
  }
  /** Table description of table tracking_raw. Objects of this class serve as prototypes for rows in queries. */
  class TrackingRaw(tag: Tag) extends Table[TrackingRawRow](tag, "tracking_raw") {
    def * = (userKey, url, trackingType, accessTimestamp) <> (TrackingRawRow.tupled, TrackingRawRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (userKey.?, url.?, trackingType.?, accessTimestamp).shaped.<>({r=>import r._; _1.map(_=> TrackingRawRow.tupled((_1.get, _2.get, _3.get, _4)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column user_key  */
    val userKey: Column[String] = column[String]("user_key")
    /** Database column url  */
    val url: Column[String] = column[String]("url")
    /** Database column tracking_type  */
    val trackingType: Column[String] = column[String]("tracking_type")
    /** Database column access_timestamp  */
    val accessTimestamp: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("access_timestamp")
    
    /** Index over (accessTimestamp) (database name tracking_raw_access_timestamp) */
    val index1 = index("tracking_raw_access_timestamp", accessTimestamp)
    /** Index over (url) (database name tracking_raw_url) */
    val index2 = index("tracking_raw_url", url)
    /** Index over (userKey) (database name tracking_raw_user_key) */
    val index3 = index("tracking_raw_user_key", userKey)
  }
  /** Collection-like TableQuery object for table TrackingRaw */
  lazy val TrackingRaw = new TableQuery(tag => new TrackingRaw(tag))
  
  /** Entity class storing rows of table TrackingSummary
   *  @param url Database column url 
   *  @param packageId Database column package_id 
   *  @param trackingType Database column tracking_type 
   *  @param count Database column count 
   *  @param runningTotal Database column running_total Default(0)
   *  @param recentViews Database column recent_views Default(0)
   *  @param trackingDate Database column tracking_date  */
  case class TrackingSummaryRow(url: String, packageId: Option[String], trackingType: String, count: Int, runningTotal: Int=0, recentViews: Int=0, trackingDate: Option[java.sql.Date])
  /** GetResult implicit for fetching TrackingSummaryRow objects using plain SQL queries */
  implicit def GetResultTrackingSummaryRow(implicit e0: GR[String], e1: GR[Int], e2: GR[java.sql.Date]): GR[TrackingSummaryRow] = GR{
    prs => import prs._
    TrackingSummaryRow.tupled((<<[String], <<?[String], <<[String], <<[Int], <<[Int], <<[Int], <<?[java.sql.Date]))
  }
  /** Table description of table tracking_summary. Objects of this class serve as prototypes for rows in queries. */
  class TrackingSummary(tag: Tag) extends Table[TrackingSummaryRow](tag, "tracking_summary") {
    def * = (url, packageId, trackingType, count, runningTotal, recentViews, trackingDate) <> (TrackingSummaryRow.tupled, TrackingSummaryRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (url.?, packageId, trackingType.?, count.?, runningTotal.?, recentViews.?, trackingDate).shaped.<>({r=>import r._; _1.map(_=> TrackingSummaryRow.tupled((_1.get, _2, _3.get, _4.get, _5.get, _6.get, _7)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column url  */
    val url: Column[String] = column[String]("url")
    /** Database column package_id  */
    val packageId: Column[Option[String]] = column[Option[String]]("package_id")
    /** Database column tracking_type  */
    val trackingType: Column[String] = column[String]("tracking_type")
    /** Database column count  */
    val count: Column[Int] = column[Int]("count")
    /** Database column running_total Default(0) */
    val runningTotal: Column[Int] = column[Int]("running_total", O.Default(0))
    /** Database column recent_views Default(0) */
    val recentViews: Column[Int] = column[Int]("recent_views", O.Default(0))
    /** Database column tracking_date  */
    val trackingDate: Column[Option[java.sql.Date]] = column[Option[java.sql.Date]]("tracking_date")
    
    /** Index over (trackingDate) (database name tracking_summary_date) */
    val index1 = index("tracking_summary_date", trackingDate)
    /** Index over (packageId) (database name tracking_summary_package_id) */
    val index2 = index("tracking_summary_package_id", packageId)
    /** Index over (url) (database name tracking_summary_url) */
    val index3 = index("tracking_summary_url", url)
  }
  /** Collection-like TableQuery object for table TrackingSummary */
  lazy val TrackingSummary = new TableQuery(tag => new TrackingSummary(tag))
  
  /** Entity class storing rows of table User
   *  @param id Database column id PrimaryKey
   *  @param name Database column name 
   *  @param apikey Database column apikey 
   *  @param created Database column created 
   *  @param about Database column about 
   *  @param openid Database column openid 
   *  @param password Database column password 
   *  @param fullname Database column fullname 
   *  @param email Database column email 
   *  @param resetKey Database column reset_key 
   *  @param sysadmin Database column sysadmin Default(false)
   *  @param activityStreamsEmailNotifications Database column activity_streams_email_notifications Default(false) */
  case class UserRow(id: String, name: String, apikey: Option[String], created: Option[java.sql.Timestamp], about: Option[String], openid: Option[String], password: Option[String], fullname: Option[String], email: Option[String], resetKey: Option[String], sysadmin: Option[Boolean]=false, activityStreamsEmailNotifications: Option[Boolean]=false)
  /** GetResult implicit for fetching UserRow objects using plain SQL queries */
  implicit def GetResultUserRow(implicit e0: GR[String], e1: GR[java.sql.Timestamp], e2: GR[Boolean]): GR[UserRow] = GR{
    prs => import prs._
    UserRow.tupled((<<[String], <<[String], <<?[String], <<?[java.sql.Timestamp], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[Boolean], <<?[Boolean]))
  }
  /** Table description of table user. Objects of this class serve as prototypes for rows in queries. */
  class User(tag: Tag) extends Table[UserRow](tag, "user") {
    def * = (id, name, apikey, created, about, openid, password, fullname, email, resetKey, sysadmin, activityStreamsEmailNotifications) <> (UserRow.tupled, UserRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, name.?, apikey, created, about, openid, password, fullname, email, resetKey, sysadmin, activityStreamsEmailNotifications).shaped.<>({r=>import r._; _1.map(_=> UserRow.tupled((_1.get, _2.get, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id PrimaryKey */
    val id: Column[String] = column[String]("id", O.PrimaryKey)
    /** Database column name  */
    val name: Column[String] = column[String]("name")
    /** Database column apikey  */
    val apikey: Column[Option[String]] = column[Option[String]]("apikey")
    /** Database column created  */
    val created: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("created")
    /** Database column about  */
    val about: Column[Option[String]] = column[Option[String]]("about")
    /** Database column openid  */
    val openid: Column[Option[String]] = column[Option[String]]("openid")
    /** Database column password  */
    val password: Column[Option[String]] = column[Option[String]]("password")
    /** Database column fullname  */
    val fullname: Column[Option[String]] = column[Option[String]]("fullname")
    /** Database column email  */
    val email: Column[Option[String]] = column[Option[String]]("email")
    /** Database column reset_key  */
    val resetKey: Column[Option[String]] = column[Option[String]]("reset_key")
    /** Database column sysadmin Default(false) */
    val sysadmin: Column[Option[Boolean]] = column[Option[Boolean]]("sysadmin", O.Default(false))
    /** Database column activity_streams_email_notifications Default(false) */
    val activityStreamsEmailNotifications: Column[Option[Boolean]] = column[Option[Boolean]]("activity_streams_email_notifications", O.Default(false))
    
    /** Index over (openid) (database name idx_openid) */
    val index1 = index("idx_openid", openid)
    /** Index over (id) (database name idx_user_id) */
    val index2 = index("idx_user_id", id)
    /** Index over (name) (database name idx_user_name) */
    val index3 = index("idx_user_name", name)
    /** Uniqueness Index over (name) (database name user_name_key) */
    val index4 = index("user_name_key", name, unique=true)
  }
  /** Collection-like TableQuery object for table User */
  lazy val User = new TableQuery(tag => new User(tag))
  
  /** Entity class storing rows of table UserFollowingDataset
   *  @param followerId Database column follower_id 
   *  @param objectId Database column object_id 
   *  @param datetime Database column datetime  */
  case class UserFollowingDatasetRow(followerId: String, objectId: String, datetime: java.sql.Timestamp)
  /** GetResult implicit for fetching UserFollowingDatasetRow objects using plain SQL queries */
  implicit def GetResultUserFollowingDatasetRow(implicit e0: GR[String], e1: GR[java.sql.Timestamp]): GR[UserFollowingDatasetRow] = GR{
    prs => import prs._
    UserFollowingDatasetRow.tupled((<<[String], <<[String], <<[java.sql.Timestamp]))
  }
  /** Table description of table user_following_dataset. Objects of this class serve as prototypes for rows in queries. */
  class UserFollowingDataset(tag: Tag) extends Table[UserFollowingDatasetRow](tag, "user_following_dataset") {
    def * = (followerId, objectId, datetime) <> (UserFollowingDatasetRow.tupled, UserFollowingDatasetRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (followerId.?, objectId.?, datetime.?).shaped.<>({r=>import r._; _1.map(_=> UserFollowingDatasetRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column follower_id  */
    val followerId: Column[String] = column[String]("follower_id")
    /** Database column object_id  */
    val objectId: Column[String] = column[String]("object_id")
    /** Database column datetime  */
    val datetime: Column[java.sql.Timestamp] = column[java.sql.Timestamp]("datetime")
    
    /** Primary key of UserFollowingDataset (database name user_following_dataset_pkey) */
    val pk = primaryKey("user_following_dataset_pkey", (followerId, objectId))
    
    /** Foreign key referencing Package (database name user_following_dataset_object_id_fkey) */
    val packageFk = foreignKey("user_following_dataset_object_id_fkey", objectId, Package)(r => r.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)
    /** Foreign key referencing User (database name user_following_dataset_follower_id_fkey) */
    val userFk = foreignKey("user_following_dataset_follower_id_fkey", followerId, User)(r => r.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table UserFollowingDataset */
  lazy val UserFollowingDataset = new TableQuery(tag => new UserFollowingDataset(tag))
  
  /** Entity class storing rows of table UserFollowingGroup
   *  @param followerId Database column follower_id 
   *  @param objectId Database column object_id 
   *  @param datetime Database column datetime  */
  case class UserFollowingGroupRow(followerId: String, objectId: String, datetime: java.sql.Timestamp)
  /** GetResult implicit for fetching UserFollowingGroupRow objects using plain SQL queries */
  implicit def GetResultUserFollowingGroupRow(implicit e0: GR[String], e1: GR[java.sql.Timestamp]): GR[UserFollowingGroupRow] = GR{
    prs => import prs._
    UserFollowingGroupRow.tupled((<<[String], <<[String], <<[java.sql.Timestamp]))
  }
  /** Table description of table user_following_group. Objects of this class serve as prototypes for rows in queries. */
  class UserFollowingGroup(tag: Tag) extends Table[UserFollowingGroupRow](tag, "user_following_group") {
    def * = (followerId, objectId, datetime) <> (UserFollowingGroupRow.tupled, UserFollowingGroupRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (followerId.?, objectId.?, datetime.?).shaped.<>({r=>import r._; _1.map(_=> UserFollowingGroupRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column follower_id  */
    val followerId: Column[String] = column[String]("follower_id")
    /** Database column object_id  */
    val objectId: Column[String] = column[String]("object_id")
    /** Database column datetime  */
    val datetime: Column[java.sql.Timestamp] = column[java.sql.Timestamp]("datetime")
    
    /** Primary key of UserFollowingGroup (database name user_following_group_pkey) */
    val pk = primaryKey("user_following_group_pkey", (followerId, objectId))
    
    /** Foreign key referencing Group (database name user_following_group_group_id_fkey) */
    val groupFk = foreignKey("user_following_group_group_id_fkey", objectId, Group)(r => r.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)
    /** Foreign key referencing User (database name user_following_group_user_id_fkey) */
    val userFk = foreignKey("user_following_group_user_id_fkey", followerId, User)(r => r.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table UserFollowingGroup */
  lazy val UserFollowingGroup = new TableQuery(tag => new UserFollowingGroup(tag))
  
  /** Entity class storing rows of table UserFollowingUser
   *  @param followerId Database column follower_id 
   *  @param objectId Database column object_id 
   *  @param datetime Database column datetime  */
  case class UserFollowingUserRow(followerId: String, objectId: String, datetime: java.sql.Timestamp)
  /** GetResult implicit for fetching UserFollowingUserRow objects using plain SQL queries */
  implicit def GetResultUserFollowingUserRow(implicit e0: GR[String], e1: GR[java.sql.Timestamp]): GR[UserFollowingUserRow] = GR{
    prs => import prs._
    UserFollowingUserRow.tupled((<<[String], <<[String], <<[java.sql.Timestamp]))
  }
  /** Table description of table user_following_user. Objects of this class serve as prototypes for rows in queries. */
  class UserFollowingUser(tag: Tag) extends Table[UserFollowingUserRow](tag, "user_following_user") {
    def * = (followerId, objectId, datetime) <> (UserFollowingUserRow.tupled, UserFollowingUserRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (followerId.?, objectId.?, datetime.?).shaped.<>({r=>import r._; _1.map(_=> UserFollowingUserRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column follower_id  */
    val followerId: Column[String] = column[String]("follower_id")
    /** Database column object_id  */
    val objectId: Column[String] = column[String]("object_id")
    /** Database column datetime  */
    val datetime: Column[java.sql.Timestamp] = column[java.sql.Timestamp]("datetime")
    
    /** Primary key of UserFollowingUser (database name user_following_user_pkey) */
    val pk = primaryKey("user_following_user_pkey", (followerId, objectId))
    
    /** Foreign key referencing User (database name user_following_user_follower_id_fkey) */
    val userFk1 = foreignKey("user_following_user_follower_id_fkey", followerId, User)(r => r.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)
    /** Foreign key referencing User (database name user_following_user_object_id_fkey) */
    val userFk2 = foreignKey("user_following_user_object_id_fkey", objectId, User)(r => r.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table UserFollowingUser */
  lazy val UserFollowingUser = new TableQuery(tag => new UserFollowingUser(tag))
  
  /** Entity class storing rows of table UserObjectRole
   *  @param id Database column id PrimaryKey
   *  @param userId Database column user_id 
   *  @param context Database column context 
   *  @param role Database column role 
   *  @param authorizedGroupId Database column authorized_group_id  */
  case class UserObjectRoleRow(id: String, userId: Option[String], context: String, role: Option[String], authorizedGroupId: Option[String])
  /** GetResult implicit for fetching UserObjectRoleRow objects using plain SQL queries */
  implicit def GetResultUserObjectRoleRow(implicit e0: GR[String]): GR[UserObjectRoleRow] = GR{
    prs => import prs._
    UserObjectRoleRow.tupled((<<[String], <<?[String], <<[String], <<?[String], <<?[String]))
  }
  /** Table description of table user_object_role. Objects of this class serve as prototypes for rows in queries. */
  class UserObjectRole(tag: Tag) extends Table[UserObjectRoleRow](tag, "user_object_role") {
    def * = (id, userId, context, role, authorizedGroupId) <> (UserObjectRoleRow.tupled, UserObjectRoleRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, userId, context.?, role, authorizedGroupId).shaped.<>({r=>import r._; _1.map(_=> UserObjectRoleRow.tupled((_1.get, _2, _3.get, _4, _5)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id PrimaryKey */
    val id: Column[String] = column[String]("id", O.PrimaryKey)
    /** Database column user_id  */
    val userId: Column[Option[String]] = column[Option[String]]("user_id")
    /** Database column context  */
    val context: Column[String] = column[String]("context")
    /** Database column role  */
    val role: Column[Option[String]] = column[Option[String]]("role")
    /** Database column authorized_group_id  */
    val authorizedGroupId: Column[Option[String]] = column[Option[String]]("authorized_group_id")
    
    /** Foreign key referencing AuthorizationGroup (database name user_object_role_authorized_group_id_fkey) */
    val authorizationGroupFk = foreignKey("user_object_role_authorized_group_id_fkey", authorizedGroupId, AuthorizationGroup)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing User (database name user_object_role_user_id_fkey) */
    val userFk = foreignKey("user_object_role_user_id_fkey", userId, User)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    
    /** Index over (context) (database name idx_uor_context) */
    val index1 = index("idx_uor_context", context)
    /** Index over (id) (database name idx_uor_id) */
    val index2 = index("idx_uor_id", id)
    /** Index over (role) (database name idx_uor_role) */
    val index3 = index("idx_uor_role", role)
    /** Index over (userId,role) (database name idx_uor_user_id_role) */
    val index4 = index("idx_uor_user_id_role", (userId, role))
  }
  /** Collection-like TableQuery object for table UserObjectRole */
  lazy val UserObjectRole = new TableQuery(tag => new UserObjectRole(tag))
  
  /** Entity class storing rows of table Vocabulary
   *  @param id Database column id PrimaryKey
   *  @param name Database column name  */
  case class VocabularyRow(id: String, name: String)
  /** GetResult implicit for fetching VocabularyRow objects using plain SQL queries */
  implicit def GetResultVocabularyRow(implicit e0: GR[String]): GR[VocabularyRow] = GR{
    prs => import prs._
    VocabularyRow.tupled((<<[String], <<[String]))
  }
  /** Table description of table vocabulary. Objects of this class serve as prototypes for rows in queries. */
  class Vocabulary(tag: Tag) extends Table[VocabularyRow](tag, "vocabulary") {
    def * = (id, name) <> (VocabularyRow.tupled, VocabularyRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, name.?).shaped.<>({r=>import r._; _1.map(_=> VocabularyRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column id PrimaryKey */
    val id: Column[String] = column[String]("id", O.PrimaryKey)
    /** Database column name  */
    val name: Column[String] = column[String]("name")
    
    /** Uniqueness Index over (name) (database name vocabulary_name_key) */
    val index1 = index("vocabulary_name_key", name, unique=true)
  }
  /** Collection-like TableQuery object for table Vocabulary */
  lazy val Vocabulary = new TableQuery(tag => new Vocabulary(tag))
  
  /** Entity class storing rows of table VsDatabaseDiagrams
   *  @param name Database column name 
   *  @param diadata Database column diadata 
   *  @param comment Database column comment 
   *  @param preview Database column preview 
   *  @param lockinfo Database column lockinfo 
   *  @param locktime Database column locktime 
   *  @param version Database column version  */
  case class VsDatabaseDiagramsRow(name: Option[String], diadata: Option[String], comment: Option[String], preview: Option[String], lockinfo: Option[String], locktime: Option[java.sql.Timestamp], version: Option[String])
  /** GetResult implicit for fetching VsDatabaseDiagramsRow objects using plain SQL queries */
  implicit def GetResultVsDatabaseDiagramsRow(implicit e0: GR[String], e1: GR[java.sql.Timestamp]): GR[VsDatabaseDiagramsRow] = GR{
    prs => import prs._
    VsDatabaseDiagramsRow.tupled((<<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[java.sql.Timestamp], <<?[String]))
  }
  /** Table description of table vs_database_diagrams. Objects of this class serve as prototypes for rows in queries. */
  class VsDatabaseDiagrams(tag: Tag) extends Table[VsDatabaseDiagramsRow](tag, "vs_database_diagrams") {
    def * = (name, diadata, comment, preview, lockinfo, locktime, version) <> (VsDatabaseDiagramsRow.tupled, VsDatabaseDiagramsRow.unapply)
    
    /** Database column name  */
    val name: Column[Option[String]] = column[Option[String]]("name")
    /** Database column diadata  */
    val diadata: Column[Option[String]] = column[Option[String]]("diadata")
    /** Database column comment  */
    val comment: Column[Option[String]] = column[Option[String]]("comment")
    /** Database column preview  */
    val preview: Column[Option[String]] = column[Option[String]]("preview")
    /** Database column lockinfo  */
    val lockinfo: Column[Option[String]] = column[Option[String]]("lockinfo")
    /** Database column locktime  */
    val locktime: Column[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("locktime")
    /** Database column version  */
    val version: Column[Option[String]] = column[Option[String]]("version")
  }
  /** Collection-like TableQuery object for table VsDatabaseDiagrams */
  lazy val VsDatabaseDiagrams = new TableQuery(tag => new VsDatabaseDiagrams(tag))
}