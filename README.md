Homework-311-lorenbak
=====================
CP311 homework due: 4/15/2014

Description: Create an application that uses and provides article data.

Use Cases
  1. User starts the application and a list of article titles are presented from the data set provided.
  2. If a User selects an article, an activity that displays the article content is displayed.
  3. When the user shakes the device, the list is refreshed and a visible indicator showing the refresh.

Requirements
  *  The application must be an Android launcher application using API level 10 or higher
  *  The application must use a content provider to expose its data publicly. It should be of the form: content://{your_package_name}/articles
  *  The application must provide the following data for each item from the content provider:
  *  Article
     o Content
     o Icon
     o Title
     o Date
     (Not every column needs a value)
  *  Load your database with the provided XML data available on the course portal in the Files section
  *  When the User shakes the device, the content is reloaded from the provided XML data
  *  Name the application “Homework 311” and name your project “Homework 311 {YOUR_UW_NET_ID}”.