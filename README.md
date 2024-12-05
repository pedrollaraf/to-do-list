# ToDoList

## 🚀 How to Set Up the Project

### 1️⃣ Configure the Base URL

- Go to [MockAPI](https://mockapi.io/).
- Get your API endpoint.
- Update the `BASE_URL` in the `app/build.gradle.kts` file for both **Debug** and **Release** modes:

  ```kotlin
  buildConfigField("String", "BASE_URL", "\"YOUR_BASE_URL\"")


### 2️⃣ Create the "tasks" Resource

- Go back to [MockAPI](https://mockapi.io/) and create a new resource named **`tasks`**.
- Use the following parameters for the resource:

  ```json
  {
    "title": "title 1",
    "description": "description 1",
    "isCompleted": false,
    "id": "1",
    "isActive": true
  }


### 3️⃣ Start with an Empty Resource

- Ensure that the **`tasks`** resource starts with no data and mock starts clean






