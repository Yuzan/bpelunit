BPELUnit
========

BPELUnit is a unit testing framework for WS-BPEL compositions. It provides facilities for deploying, invoking and undeploying a composition running in a WS-BPEL engine. External services can be replaced with mockups and replies can be delayed at will. Additionally, BPELUnit can derive test cases by reading rows from an external data source and combining it with Apache Velocity templates.

More documentation is available in the directory ``org.bpelunit.dist/src/main/resources/doc``. The rest of this document is dedicated to explaining the build infrastructure available.

Basic structure of the code
---------------------------

On a high level, BPELUnit is divided into:

* A core library with all the "real" testing logic: ``org.bpelunit.framework``.
* A set of interfaces to access the core logic:
  * From the command line: ``org.bpelunit.framework.client.command``.
  * From Ant: ``org.bpelunit.framework.client.ant``.
  * From Maven: ``org.bpelunit.framework.client.maven``.
* A set of external data source types for various file formats (the core library only has support for Apache Velocity ``.vm`` files):
  * CSV: ``org.bpelunit.framework.control.datasource.csv``.
  * XLS and XLSX: ``org.bpelunit.framework.control.datasource.excel``.
  * HTML tables: ``org.bpelunit.framework.control.datasource.html``.
  * ooCalc: ``org.bpelunit.framework.control.datasource.ods``.
* A small utility for inlining a data source into a BPELUnit test specification file: ``org.bpelunit.utils.datasourceinliner``.

These are regular Java projects. However, the core library and the data source types are also available as OSGi bundles. These are used by the Eclipse plugins, feature and update site defined under the ``tycho`` folder.

Building with Maven
-------------------

In order to build the code with Maven 3.0 or higher (needed for the Tycho-based projects), you only need to run these commands:

    mvn install
    cd tycho
    mvn install
    cd ..
    cd org.bpelunit.dist
    mvn install

This will build and install into your local Maven repository everything: core projects, Eclipse plugins, the Eclipse feature and update site, and the standalone distribution.

Please note that due to a limitation in Tycho, the core projects and the Eclipse plugins have been split into separate reactors. Also, you *must* install the core projects into your Maven repository before you can build the Tycho-based projects or the ``org.bpelunit.dist`` project.

Building with Eclipse PDE
-------------------------

If you want, you can also work on the BPELUnit code straight from the Eclipse Plug-in Development Environment. To do so, you will still need Maven to bootstrap the environment. Just run this from this directory:

    mvn compile

That will ensure that the dependencies will be available in the ``target/dependency`` directory in each core project, and that the required code is automatically generated.

You should now import the projects into your Eclipse workspace. You may need to define the M2_REPO variable in the "Java Build Path" section of one of the projects if you have not done so yet. It needs to point to your local Maven repository: on most UNIX-based systems, it should be ``$HOME/.m2/repository``. Please consult the Maven documentation for more details.

To test them out, right-click on any of the projects and select "Run As > Eclipse Application". This will open a nested Eclipse instance with the BPELUnit plugins running inside it.