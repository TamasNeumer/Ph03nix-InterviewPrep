# LifeRay

#### Blade

- Blade helps to build and customize Liferay projects.
- Your workspace is the home for all your custom Liferay projects.
- Creating workspace: `blade init [WORKSPACE_NAME]`
- Workspace structure:
  - `bundles`: the default folder for Liferay Portal bundles.
  - `configs`: config files for different environments.
  - `gradle`: Gradle Wrapper for workspace
  - `modules`: Your own custom modules
  - `themes`: holds your custom themes which are built using the Theme Generator.
  - `build.gradle`: the common Gradle build file.
  - `gradle-local.properties`: sets user-specific properties for your workspace
  - `gradle.properties`: specifies the workspace’s project locations and Liferay Portal server configuration globally.
  - `gradlew`: executes the Gradle command wrapper
  - `settings.gradle`: applies plugins to the workspace and configures its dependencies
  - `wars`: holds traditional WAR-style web application projects.
- Start server: `blade server start -b`
- Create projects based on templates
  - `blade create [OPTIONS] <NAME>`
  - `blade create -t mvc-portlet -p com.liferay.docs.guestbook -c GuestbookPortlet my-guestbook-project`
- Deploy
  - `blade deploy`
- Stop
  - `blade server stop`


#### OSGi

- The OSGi specification describes a modular system and a service platform for the Java programming language that implements a complete and dynamic component model, something that does not exist in standalone Java/VM environments.
- Applications or components, coming in the form of bundles for deployment, can be remotely installed, started, stopped, updated, and uninstalled without requiring a reboot; management of Java packages/classes is specified in great detail.

- **Bundles**
  - Bundles are normal JAR components with extra manifest headers.
- **Services**
  - The services layer connects bundles in a dynamic way by offering a publish-find-bind model for plain old Java interfaces (POJIs) or plain old Java objects (POJOs).
- **Services Registry**
  - The application programming interface for management services (ServiceRegistration, ServiceTracker and ServiceReference).
- **Life-Cycle**
  - The application programming interface for life cycle management (install, start, stop, update, and uninstall) for bundles.
- **Modules**
  - The layer that defines encapsulation and declaration of dependencies (how a bundle can import and export code).
- **Security**
  - The layer that handles the security aspects by limiting bundle functionality to pre-defined capabilities.

#### Portlet standard

- The **Java Portlet Specification** defines a contract between the **portlet container** and **portlets** and provides a convenient programming model for Java portlet developers.
  - Portlet
    - Portlets are pluggable user interface software components that are managed and displayed in a web portal, for example an enterprise portal or a web CMS.
    - Portlets produce fragments of markup (HTML, XHTML, WML) that are aggregated into a portal.
  - Portlet container
    - A Portlet container runs portlets and provides them with the required runtime environment. A portlet container contains portlets and manages their life cycles. It also provides persistent storage mechanisms for the portlet preferences.

#### JSP

- JavaServer Pages (JSP) is a technology that helps software developers create dynamically generated web pages based on HTML, XML, or other document types. Released in 1999 by Sun Microsystems,[1] JSP is similar to PHP and ASP, but it uses the Java programming language.