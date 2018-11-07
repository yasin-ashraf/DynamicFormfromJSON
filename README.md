# Assignment for Hubbler
Assignment done by me as part of recruitment process at hubbler.mobi

Recruitment process managed and overseen by Renjith Chaz, CTO Hubbler

# Problem

There will be two screens in this assignment. 
Total reports will be zero initially as you haven't added any reports.
When you click on the Add button, the second screen is shown. 
The second screen is completely dynamic. You have to keep a JSON Array to create this screen. 

For example consider the following JSON:

[

{'field-name':'name', 'type':'text', 'required':true},

{'field-name':'age', 'type':'number', 'min':18, 'max':65},

{'field-name':'address', 'type':'multiline'}

]

If input JSON is as below:

[

{'field-name':'name', 'type':'text'},

{'field-name':'age', 'type':'number'},

{'field-name':'gender', 'type':dropdown', 'options':['male', 'female', 'other']},

{'field-name':'address', 'type':'multiline'}

]

Note: The drop-down field can be displayed as a spinner.

Suppose for the above screen, you entered the name as 'Hari', age: 29, gender: 'male' and Address: 'Oxford Engg College, Bangalore, Karnataka'

After entering these data, if you hit 'Done' button, you should prepare a JSON in the following format:

{'Name':'Hari', 'Age':29, 'Gender':'male', 'Address':'Oxford Engg College, Bangalore, Karnataka' }

You may print above data as a log statement
Once you fill in data and click the Done button, control goes back to the 1st page. and it will look like shown below: 
The listing page should display 1st two fields data. If you have only 1 field, display just that. 

About Validations:

required, min, max etc. work as expected; that is if a field has 'required': true, and if you leave it unfilled,  upon tapping the done button, an error msg/toast shall be displayed. For min, max the value entered should be within the range. Else as before an error toast/msg will be displayed upon done button tap.

# Solution

App is named hubbler with an icon 'Ya'.

First screen of the app shows the added reports (an empty view if there is none), total number of reports at the top right corner (initially 0) and a 'ADD REPORT' button at the bottom.

On clicking the ADD Report Button, second screen is shown.Form Fields are generated dynamically after reading .json file from Assets folder. A json object is created at the beginning and updated evrytime user types into the fields. EditText validation are done when the user types in the field using TextWatcher. Each time user types the json object is logged in the console.
If a field is set as required, a tag of 'required' is set with corresponding edittext and is checked at the end. First item on the options list is set as the default value of Spinner.

SQLite Database with Room is used to store the added json Objects as string. On clicking 'Done' the report is added to Datbase and control is back to previous activity that shows the added report with a recyclerview adapter.
