# Android ML-Kit Demo for Face and Object Detection in Jetpack Compose

## Table of Contents
- [What is this repository for?](#what-is-this-repository-for)
- [Overview](#overview)
- [Demo Screenshots](#demo-screenshots)
- [ML Kit Vision Features](#ml-kit-vision-features)
- [Installation](#installation)
- [Usage](#usage)


## What is this repository for?
This repository is a Jetpack Compose project that showcases the use of ML Kit Vision for face and object detection on Android devices. ML Kit Vision is a powerful tool from Google that allows you to integrate vision-based machine learning features into your Android applications.

## Overview
The primary focus of this repository is to demonstrate how to implement face and object detection functionalities using ML Kit Vision in a Jetpack Compose application. The project aims to provide a user-friendly and interactive experience, displaying the detected results in real-time.
The live camera can detect face and object together.

- The application will create a directory named "CameraMLDemo" in the internal storage of the Android device. The directory path will be "/storage/media/CameraMLDemo". 

- After successful detection, the face and object data will be saved as separate image files in the storage directory. The image files will be named with unique identifiers for face and object.

- Face images will be named as "face_id_1.jpg", "face_id_2.jpg", and so on, where "id" is the unique identifier for each face detected.

- Object images will be named as "object_id_1.jpg", "object_id_2.jpg", and so on, where "id" is the unique identifier for each object detected.



## Demo Screenshots
Here are some screenshots showcasing the app's capabilities:

![Screenshot 1](/CameraMLDemo/screenshots/1.png) ![Screenshot 2](/CameraMLDemo/screenshots/2.png)

## ML Kit Vision Features
The app demonstrates the following ML Kit Vision features:
* [Face Detection](https://developers.google.com/ml-kit/vision/face-detection?hl=en): Detects facial features in close-range images.

* [Object Detection](https://developers.google.com/ml-kit/vision/object-detection?hl=en): Localizes and tracks one or more objects in real-time using the live camera feed.

## Installation
To install and run the app, follow these steps:
1. Clone this repository to your local machine using `git clone`.
2. Open the project in Android Studio or your preferred IDE.
3. Build and run the app on your Android device or emulator.

Make sure you have the necessary SDK versions and dependencies installed.

## Usage
Upon launching the app, you will be presented with a live camera feed. The app will automatically detect and highlight faces and objects within the camera's view. Interact with the app to experience the real-time detection capabilities.
