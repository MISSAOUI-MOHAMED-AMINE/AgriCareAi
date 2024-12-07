# AgriCareAI - Empowering Farmers with Intelligent Agricultural Solutions

**AgriCareAI** is a mobile application developed in **Java** using **Android Studio**, designed to assist farmers in improving their crops and agricultural practices. The app integrates several advanced services that combine weather forecasting, plant disease detection, interactive chatbot functionality, and offline resources. Here's a comprehensive overview of the project's key features:


---

## Features and Implementation

### 1. **Weather Dashboard Forecasting**
AgriCareAI provides a real-time weather dashboard tailored for farmers in Tunisia. 
![pro_img (1)](https://github.com/user-attachments/assets/caaf2a3a-97a6-4393-8dac-b64727ac4266)



- **Data Extraction**: 
  - Weather data for various Tunisian cities was scraped from **AccuWeather** using **Selenium**.
- **ETL Pipeline**:
  - An **ETL pipeline** was built in **Python** to automate data processing.
  - The pipeline stores data in an **Azure SQL Database** for persistence.
  - Orchestration was implemented using **Apache Airflow** for scheduled tasks and workflow management.
- **Dashboard Design**:
  - A visually appealing and interactive weather dashboard was created in **Power BI**.
  - The dashboard was published on **Power BI Cloud** for easy accessibility.
- **Integration in App**:
  - The dashboard is seamlessly integrated into AgriCareAI through a **WebView** component in **Android Studio**.

---

### 2. **Plant Leaf Disease Detection**

<img src="https://github.com/user-attachments/assets/2738c698-19fd-47f5-bbc7-995d2bfa55ed" width="200" />

This feature enables farmers to identify and diagnose plant leaf diseases through image analysis.

- **Model Development**:
  - Pretrained object detection models were fine-tuned on a custom dataset from **Roboflow** to detect diseased zones and classify the diseases.
  - Models evaluated: **YOLOv7**, **YOLOv8**, and **DETR**.
  - The model with the best accuracy metrics was selected.
- **Deployment**:
  - The trained model was converted to **TensorFlow Lite (TFLite)** for mobile compatibility.
  - Integrated into the Android app to allow real-time disease detection through the device's camera.

---

### 3. **Agricultural Chatbot**
AgriCareAI includes a powerful chatbot to answer farmers' questions and provide tailored agricultural advice.
![image](https://github.com/user-attachments/assets/aa2b4e34-dab5-4ab6-99d1-bf4b18729aa6)

- **Model Selection**:
  - **MetaLLaMA 3 8B Instruct**, a large language model (LLM) from **Hugging Face**, was chosen for its performance and contextual accuracy.
- **Interactive Features**:
  - **Voice-to-Text**: Converts voice inputs into text for interaction.
  - **Text-to-Speech**: Vocalizes chatbot responses for ease of understanding.
- **API Integration**:
  - Implemented through API requests for dynamic and efficient communication between the app and the LLM.

---

### 4. **Offline Encyclopedia**
This feature ensures users can access critical information about detected diseases even without an internet connection.

- **Content Design**:
  - Brief and concise disease information (e.g., name, definition, symptoms, causes, and solutions).
- **Data Storage**:
  - All data is stored in a **JSON file** for quick access and offline functionality.

---

## Security and User Authentication
AgriCareAI prioritizes user security with robust authentication mechanisms:
- **Login and Signup**:
  - Users must register and authenticate using their credentials.
- **Data Storage**:
  - User data is securely stored in a **local SQLite database** within the app.

---

## Key Technologies and Tools
- **Programming Languages**: Java (Android Studio), Python
- **Deep Learning Models**: YOLOv7, YOLOv8, DETR
- **Libraries and Tools**: TensorFlow Lite, Selenium, Power BI, Apache Airflow
- **Databases**: Azure SQL Database, SQLite
- **APIs**: Hugging Face, TensorFlow Lite Interpreter

---

## How to Run the App
1. Clone the repository:  
   ```bash
   git clone https://github.com/MISSAOUI-MOHAMED-AMINE/AgriCareAi.git
   ```
2. Open the project in **Android Studio**.
3. Install the required dependencies and SDKs.
4. Run the app on an emulator or Android device.

---

AgriCareAI bridges advanced technologies with practical farming needs, making it a valuable tool for modern agriculture. Contributions and suggestions are welcome!


https://github.com/user-attachments/assets/55b272ad-2816-4935-a019-c03edcb44843

