# Social Connect  

**Social Connect** is a modern social media application designed with a focus on performance, scalability, and user experience. Built using **Kotlin** and adhering to **Clean Architecture**, the app integrates cutting-edge technologies to deliver a seamless social networking platform.  

---

## 🌟 Features  
### 🔒 Authentication  
- **Register** new users securely.  
- **Login** using email and password.  
- **Forgot Password** functionality to reset access.  

### 🖼️ Posts & Media  
- **Add Posts** with support for multiple media (images and videos).  
- **Stories**: Visible for 24 hours or save them to highlights.  
- **Reels**: Share short, engaging video content.  

### 💬 Real-Time Messaging  
- **Chat** with friends in real-time.  
- Send **text**, **images**, **videos**, and **audio recordings**.  

### 🛡️ Social Features  
- **Block Users** for privacy and safety.  
- View **Mutual Friends** for better social connections.  
- **Comments and Likes** on posts to engage with others.  
- **Recent Activities** to track social interactions.  
- **Save Items** for later access.  

### 🌍 Customization  
- **Multi-language Support**: Arabic and English.  
- **Dark Mode** for enhanced usability in low-light environments.  

---

## 🛠️ Technology Stack  
### 🔗 Backend  
- [**Firebase Firestore**](https://firebase.google.com/products/firestore): Real-time database for scalable storage.  
- [**Firebase Authentication**](https://firebase.google.com/products/auth): Secure user login and management.  
- [**Firebase Cloud Messaging (FCM)**](https://firebase.google.com/products/cloud-messaging): Notifications and real-time messaging.  
- [**Firebase Storage**](https://firebase.google.com/products/storage): Media storage solution.  

### 📱 Frontend  
- **[Kotlin](https://kotlinlang.org/)**: Modern, robust programming language for Android development.  
- **[Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)**: Asynchronous programming for better performance.  
- **[Hilt](https://developer.android.com/training/dependency-injection/hilt-android)**: Dependency injection framework.  
- **[Retrofit](https://square.github.io/retrofit/)**: Networking and API integration.  
- **[Navigation Component](https://developer.android.com/guide/navigation/)**: Simplified app navigation.  
- **[DataStore](https://developer.android.com/topic/libraries/architecture/datastore)**: Local storage for user preferences.  
- **[SSP/SDP](https://github.com/intuit/ssp)**: Scalable pixel utilities for responsive UI.  

### 🎨 Media & Graphics  
- **[Glide](https://github.com/bumptech/glide)**: Efficient image loading and caching.  
- **[CircleImageView](https://github.com/hdodenhof/CircleImageView)**: Circular image display.  
- **[PhotoView](https://github.com/Baseflow/PhotoView)**: Advanced pinch-to-zoom functionality.  
- **[ImagePicker](https://github.com/Dhaval2404/ImagePicker)**: Select images and videos from the gallery or camera.  
- **[LightCompressor](https://github.com/AbedElazizShe/LightCompressor)**: Video compression tool.  
- **[CameraX](https://developer.android.com/training/camerax)**: Camera library for capturing photos and videos.  
- **[Lottie](https://lottiefiles.com/)**: Animated illustrations for engaging UI.  

### 🎙️ Communication  
- **[Zego Cloud](https://www.zegocloud.com/)**: High-quality audio and video calling.  
- **[RecordView](https://github.com/3llomi/RecordView)**: Record and send audio messages.  

### 📦 Miscellaneous  
- **[ZXing](https://github.com/zxing/zxing)**: QR code scanning library.  
- **[Shimmer Layout](https://facebook.github.io/shimmer-android/)**: Beautiful loading animations.  
- **[ExoPlayer](https://exoplayer.dev/)**: Media playback library.  
- **[ViewPagerIndicator](https://github.com/zhpanvip/viewpagerindicator)**: Swipe indicators for ViewPager.  

---

## 📂 Project Structure  
The app is built using **MVVM** (Model-View-ViewModel) architecture, adhering to **SOLID principles** and other common design patterns. This ensures:  
- **Scalability**: Easily extendable for future features.  
- **Maintainability**: Cleaner and modular codebase.  
- **Testability**: Seamless unit and UI testing.
