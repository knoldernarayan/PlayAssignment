package models
import java.util.Date
import java.sql.{ Date => SqlDate }
import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.Tag
import java.sql.Timestamp
import java.text.SimpleDateFormat
/**
 * This is Login case class which describes Login's credentials,
 * required for login to the system.
 * 
 * @author Narayan
 */
case class Login(email: String, password: String)
/**
 * This is KnolxUser case class which describes KnolxUser's credentials and
 * required for persist user data into database.
 * 
 * @author Narayan
 */
case class KnolxUser(id: Option[Int], name: String, address: String, company: String, email: String, password: String, phone:Long, user_type: Int, created: Date = new Date, updated: Date = new Date)
/**
 * This is KnolxUsers Table class which creates Knolx_user_tab table in database.
 * 
 * @author Narayan
 */
class KnolxUsers(tag: Tag) extends Table[KnolxUser](tag, "knolxusertab") {

  implicit val util2sqlDateMapper = MappedColumnType.base[java.util.Date, java.sql.Date](
    { utilDate => new java.sql.Date(utilDate.getTime()) },
    { sqlDate => new java.util.Date(sqlDate.getTime()) })

  def id: Column[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name: Column[String] = column[String]("name", O.NotNull)
  def address: Column[String] = column[String]("address", O.NotNull)
  def company: Column[String] = column[String]("company", O.NotNull)
  def email: Column[String] = column[String]("email", O.NotNull)
  def password: Column[String] = column[String]("password", O.NotNull)
  def phone: Column[Long] = column[Long]("phone", O.NotNull)
  def user_type: Column[Int] = column[Int]("user_type", O.NotNull)
  def created: Column[Date] = column[Date]("created", O.NotNull)
  def updated: Column[Date] = column[Date]("updated", O.NotNull)
  def * = (id.?, name, address, company, email, password, phone, user_type, created, updated) <> (KnolxUser.tupled, KnolxUser.unapply)
}
/**
 * KnolxUserObject is singleton object which holds all database operations.
 * 
 * @author Narayan
 */
object KnolxUserObject {

  val knolxTable = TableQuery[KnolxUsers]
  /**
   * insertKnol is a method which inserts new KnolxUser into database.
   *
   * @param knol          knol is object of KnolxUser case class.
   * @param session       session is implicit object of Session class which creates session with database.
   * @return              this method returns Integer value which indicates number of inserted rows.
   */
  def insertKnol(knol: KnolxUser)(implicit session: Session): Int = {
    knolxTable.insert(knol)
  }
  /**
   * updateKnolById is a method which updates the given KnolxUser in database.
   *
   * @param id            id is a Integer value which is unique for each KnolxUser.
   * @param knol          knol is object of KnolxUser case class.
   * @param session       session is implicit object of Session class which creates session with database.
   * @return              this method returns Integer value which indicates number of updated rows.
   */
  def updateKnolById(email:String, knol: KnolxUser)(implicit s: Session): Int = {
    knolxTable.filter(_.email ===email).update(knol)
  }
  /**
   * loginFormValidation is a method which validates the login form which is entered by user.
   *
   * @param loginFormObject   loginFormObject is instance of Login case class.
   * @param session           session is implicit object of Session class which creates session with database.
   * @return                  this method returns Integer value which indicates unique id .
   */
  def loginFormVaildation(loginFormObject: Login)(implicit s: Session): String = {
   val knol= knolxTable.filter(x => x.email === loginFormObject.email && x.password === loginFormObject.password).list.head
   if(knol.id.get > 0) knol.name
   else "no user"
  }
  /**
   * editKnolById is a method which filters the particular KnolxUser from database on the basis of id.
   *
   * @param email         email is a email type value which is unique for each KnolxUser.
   * @param session       session is implicit object of Session class which creates session with database.
   * @return              this method returns Option[KnolxUser] object which is for from binding.
   */
 
  def getKnolByEmail(email:String)(implicit s: Session):Option[KnolxUser] = {
    knolxTable.filter(_.email === email).firstOption
  }
}