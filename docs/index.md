easy-add-files-to-bag
===========
[![Build Status](https://travis-ci.org/DANS-KNAW/easy-add-files-to-bag.png?branch=master)](https://travis-ci.org/DANS-KNAW/easy-add-files-to-bag)

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
       -l, --log-file  <arg>   The name of the logfile in csv format. If not provided a file
                               easy-add-files-to-bag-<timestamp>.csv will be created in the home-dir of the user.
                               (default = easy-add-files-to-bag-2020-02-02T20:20:02.000Z.csv)
       -m, --metadata  <arg>   Existing CSV file specifying the files and metadata to add to the bags
       -h, --help              Show help message
       -v, --version           Show version of this program

EXAMPLES
--------

    easy-add-files-to-bag -d vaultDir -f twisterDir -d fedor2vault.csv -m metadata.csv

CSV files
---------

### datasets.csv

| column name | column description |
|-------------|--------------------|
| fedoraID    | easy-dataset-id, may be listed in `metadata` CSV file |
| UUID        | file name. If the fedoraId is mentioned in `metadata` CSV file, the UUID must exist in the `bags` directory. |

Additional columns are ignored. The first line is ignored, assuming ot is a header.
An example is the `log-file` produced by [easy-fedoara2vault](https://github.com/DANS-KNAW/easy-fedora2vault#resulting-files).

### metadata.csv

| column name        | column description |
|--------------------|--------------------|
| path               | Original path on Twister |
| keep               | Lines without the value `YES` are ignored. |
| groupNr            | Ignored |
| accessibleToRights | Optional. Default from the dataset |
| fedoraId           | dataset the file belongs to. Lines without a value are ignored. |

Additional columns are ignored. The first line is ignored, assuming ot is a header.

### log-file.csv

| column name | column description |
|-------------|--------------------|
| path        | copied from metadata.csv |
| rights      | idem |
| fedoraID    | idem |
| comment     | error message or a message containing the item added to `metadata/files.xml` and the location of the added file |

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
