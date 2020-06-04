easy-add-files-to-bag
===========
[![Build Status](https://travis-ci.org/DANS-KNAW/easy-add-files-to-bag.png?branch=master)](https://travis-ci.org/DANS-KNAW/easy-add-files-to-bag)

<!-- Remove this comment and extend the descriptions below -->


SYNOPSIS
--------

    easy-add-files-to-bag -b <bags-dir> -f <files-dir> -m <metadata-csv-file> -d <dataset-csv-file>


DESCRIPTION
-----------

add files to existing bags


ARGUMENTS
---------

    Options:

       -b, --bags  <arg>       Directory containing existing bags
       -d, --datasets  <arg>   Existing CSV file mapping fedora-IDs to UUID-s
       -f, --files  <arg>      Directory containing files specified in the path column of the metadata CSV
       -m, --metadata  <arg>   Existing CSV file specifying the files and metadata to add to the bags
       -h, --help              Show help message
       -v, --version           Show version of this program
       -h, --help      Show help message
       -v, --version   Show version of this program

    Subcommand: run-service - Starts EASY Add Files To Bag as a daemon that services HTTP requests
       -h, --help   Show help message
    ---

EXAMPLES
--------

    easy-add-files-to-bag -d vaultDir -f twisterDir -d fedor2vault.csv -m metadata.csv

CSV files
---------

### datasets.csv

| column name | column description |
|-------------|--------------------|
| fedoraID    | easy-dataset-id, may be listed in `metadata` CSV file |
| UUID        | file name, must exist in `bags` directory |

Subsequent columns are ignored.
An example is the `log-file` produced by [easy-fedoara2vault](https://github.com/DANS-KNAW/easy-fedora2vault#resulting-files).
Additional columns are ignored.

### metadata.csv

| column name | column description |
|-------------|--------------------|
| path | Original path on Twister |
| keep | Should the file be archived? YES/NO |
| group-nr | Optional. Groups files not in Fedora. |
| accessibleToRights | Optional. Default from the dataset |
| fedora-id | dataset the file belongs to |
| metadata in fedora | er is geen directe dataset in fedora beschikbaar. dit fedora-id geeft de metadata, maar deze fedora-id mag NIET in het <identifier> veld opgenomen worden, maar in een `<relation>` |

INSTALLATION AND CONFIGURATION
------------------------------
Currently this project is built as an RPM package for RHEL7/CentOS7 and later. The RPM will install the binaries to
`/opt/dans.knaw.nl/easy-add-files-to-bag` and the configuration files to `/etc/opt/dans.knaw.nl/easy-add-files-to-bag`. 

To install the module on systems that do not support RPM, you can copy and unarchive the tarball to the target host.
You will have to take care of placing the files in the correct locations for your system yourself. For instructions
on building the tarball, see next section.

BUILDING FROM SOURCE
--------------------
Prerequisites:

* Java 8 or higher
* Maven 3.3.3 or higher
* RPM

Steps:
    
    git clone https://github.com/DANS-KNAW/easy-add-files-to-bag.git
    cd easy-add-files-to-bag 
    mvn clean install

If the `rpm` executable is found at `/usr/local/bin/rpm`, the build profile that includes the RPM 
packaging will be activated. If `rpm` is available, but at a different path, then activate it by using
Maven's `-P` switch: `mvn -Pprm install`.

Alternatively, to build the tarball execute:

    mvn clean install assembly:single
