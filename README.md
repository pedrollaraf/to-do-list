# ToDoList


HOW TO MAKE IT WORK.

GO TO https://mockapi.io/
GET YOUR ENDPOINT AND CHANGE BASE_URL IN app/build.gradle.kts DEBUG MODE AND RELEASE MODE
buildConfigField("String","BASE_URL", "\"YOUR_BASE_URL"")

AFTER THAT GO TO THE WEB MOCK AGAIN AND CREATE A NEW RESOURCE WITH "tasks" NAME AND FOLLOWING THE PARAMETERS BELOW:

{
"title":"title 1",
"description":"description 1",
"isCompleted":false,
"id":"1",
"isActive": true
}

MAKE SURE THE CONTENT STARTS EMPTY.






