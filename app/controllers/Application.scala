package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.db.slick._
import play.api.Play.current
import models._
import java.util.Date
/**
 * Application is singleton object which extends play Controller
 * and holds all Action or DBAction user defined methods.
 *
 * @author Narayan
 */
object Application extends Controller {
  /**
   * This is registrationForm which is used for mapping with KnolxUser case class.
   *
   * @param   All credentials of KnolxUser case class.
   */
  var userName ="no user"
  val registrationForm = Form(
    mapping(
      "id" -> ignored(Some(0): Option[Int]),
      "name" -> nonEmptyText,
      "address" -> nonEmptyText,
      "company" -> nonEmptyText,
      "email" -> email,
      "password" -> nonEmptyText,
      "phone" -> longNumber,
      "user_type" -> number,
      "created" -> ignored(new Date),
      "updated" -> ignored(new Date))(KnolxUser.apply)(KnolxUser.unapply))
  /**
   * This is loginForm which is used for mapping with Login case class.
   *
   * @param   All credentials of Login case class.
   */
  val loginForm = Form(
    mapping(
      "email" -> email,
      "password" -> nonEmptyText)(Login.apply)(Login.unapply))

  /**
   * This is index Action which renders the views.html.index play Template.
   * @return      http Response Ok.
   */

  def index = Action {
    Ok(views.html.index("home"))
  }

  /**
   * This is create Action which renders the views.html.registration play Template.
   * @return      http Response Ok.
   */

  def create = Action {
    Ok(views.html.registration(registrationForm))
  }

  /**
   * This is login Action which renders the views.html.login play Template.
   * @return      http Response Ok.
   */

  def login = Action {implicit request=>
    Ok(views.html.loginForm(loginForm))
  }

  /**
   * This is save DBAction which use is bind the registration form with httpRequest.
   * @return      if any error in request the returns BadRequest Response
   *              otherwise insert the form value into database and redirect to index page.
   */

  def save = DBAction { implicit request =>
    registrationForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.registration(formWithErrors)),
      knolxuser => {
        KnolxUserObject.insertKnol(knolxuser)
        val msg = s"Company ${knolxuser.name} has been created"
        Redirect(routes.Application.index)
      })
  }
  /**
   * This is loginFormValidate DBAction which use is bind the loginForm form with httpRequest.
   * @return      if any error in request the returns BadRequest Response
   *              otherwise validate the login credentials and create session.
   */

  def loginFormValidate = DBAction { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.loginForm(formWithErrors)),
      knolxuser => {
         userName = KnolxUserObject.loginFormVaildation(knolxuser)
        if (userName!="no user") {
          val msg = s"KnolxUser  ${knolxuser.email} has been logined"
          Ok(views.html.userHome("Logout",userName)).withSession("email" -> knolxuser.email).flashing("success" -> "Hello this is a flash message!")
        } else {
          val msg = s"KnolxUser  ${knolxuser.email} has been not logined"
          Ok(msg)
        }
      })
  }

  /**
   * This is userHome Action which renders the views.html.userHome play Template.
   * @return      http Response Ok.
   */

  def userHome(loginStatus: String) = Action {implicit request =>
    Ok(views.html.userHome("Logout",userName)).flashing("success"->"Hello this is a flash message!")
  }

  /**
   * This is logOut Action which renders the views.html.index play Template.
   * @return      http Response Ok.
   */
  def logOut = Action {
    Redirect(routes.Application.index).withNewSession
  }

  /**
   * This is edit DBAction which use is fill the registrationForm form.
   * @return      if user not in database returns NotFound response
   *              otherwise fill the registrationForm.
   */

  def edit() = DBAction { implicit request =>
    KnolxUserObject.getKnolByEmail(request.session.get("email").get) match {
      case Some(knolxUser) => Ok(views.html.editForm(registrationForm.fill(knolxUser),userName))
      case None            => NotFound
    }
  }

  /**
   * This is update DBAction which use to update the registrationForm data.
   * @return      if any error in request the returns BadRequest Response
   *              otherwise  update the credentials .
   */

  def update() = DBAction { implicit request =>
    registrationForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.editForm(formWithErrors,userName)),
      user => {
        val knol = KnolxUserObject.getKnolByEmail(request.session.get("email").get).get
        val KnolxUserToUpdate: KnolxUser = user.copy(knol.id, created = knol.created)
        KnolxUserObject.updateKnolById(request.session.get("email").get, KnolxUserToUpdate)
        Ok(views.html.userHome("Logout",userName))
      })
  }

}