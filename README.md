# clojure-googlesheets

1- Get OAuth credentials from google console
	- Navigate to the Developers Console https://console.developers.google.com/. Click on Enable and manage APIs and select Create a new project. Enter the project name and click Create.
	- Once project is created, click on Credentials in the sidebar, then the Create Credentials drop-down. As our client is going to run from cron, we want to enable server-to-server authentication, so select Service account key. On the next screen, select New service account and enter a name. Once new service account created, click on it and then Add Key, make sure the JSON radio button is selected, then click on Create.
	- Copy the downloaded JSON file into your project’s resources directory. It should look something like:
	{
  "type": "service_account",
  "project_id": "gmavenproject",
  "private_key_id": "041db3d758a1a7ef94c9c59fb3bccd2fcca41eb8",
  "private_key": "-----BEGIN PRIVATE KEY-----\n...\n-----END PRIVATE KEY-----\n",
  "client_email": "clojureserviceaccount@gmavenproject.iam.gserviceaccount.com",
  "client_id": "106215031907469115769",
  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
  "token_uri": "https://oauth2.googleapis.com/token",
  "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
  "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/clojureserviceaccount%40gmavenproject.iam.gserviceaccount.com"
}
	- In core.clj, line 23, update json filename to yours
	- We’ll use this in a moment to create a GoogleCredential object, but before that navigate to Google Sheets and create a test spreadsheet. Grant read access to the spreadsheet to the email address found in client_email in your downloaded credentials. For example here: clojureserviceaccount@gmavenproject.iam.gserviceaccount.com

2- Google sheet sample link: https://docs.google.com/spreadsheets/d/1KWG2TT5eupJpzOL4tZq_gGMcT6rTQPlojRLvR15Kc9k/edit#gid=0
+ Content of google sheet is got from the first lesson (playcards)
+ If you want to change to another google sheet with same format as above sample, let go to core.clj, line 91, edit "Playcards" to your google sheet name.

3- Script is using in-memory database, so database will be create once runing script. If you want to make sure database working well after running scrip, let go to last line and uncomment following
;(sql/query db ["select * from playingcards where matchnumber=1"])
Then C-x C-e to see returned result


## Installation

Download from http://example.com/FIXME.

## Usage

FIXME: explanation

    $ java -jar clojure-googlesheets-0.1.0-standalone.jar [args]

## Options

FIXME: listing of options this app accepts.

## Examples

...

### Bugs

...

### Any Other Sections
### That You Think
### Might be Useful

## License

Copyright © 2021 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
