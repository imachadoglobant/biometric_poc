# BiometricApp

POC covering the following use cases:

- Dummy login with random token generation
- Username and token encryption
- User data local storage
- Biometric enrollment
- Biometric authentication

Tools used:

- <a target="_blank" href="https://developer.android.com/training/dependency-injection/dagger-android">
  Dagger</a> for Dependency Injection
- <a target="_blank" href="https://developer.android.com/jetpack/androidx/releases/compose">Jetpack
  Compose</a> for UI components
- <a target="_blank" href="https://developer.android.com/develop/ui/compose/navigation">Compose
  Navigation</a> for screen transitions
- <a target="_blank" href="https://developer.android.com/privacy-and-security/keystore">Android
  Keystore system</a> for data encryption
- <a target="_blank" href="https://developer.android.com/jetpack/androidx/releases/room">Jetpack
  Room</a> with Coroutines and Kotlin Flows for data storage
- <a target="_blank" href="https://developer.android.com/jetpack/androidx/releases/biometric">
  AndroidX Biometric library</a> for biometric enrollment and authentication

Key features:

- User's personal information is stored using KeyStore to encrypt it and Jetpack Room to store it
- User's non-sensitive data is stored in plain text using Jetpack Room
- Reading/writing access to user's info are thread-safe Room transactions
- Biometric processes are securely executed using Keystore system
- Biometric enrollment and authentication is based on user's token
- Biometric data provided by AndroidX is stored in Room

## Android KeyStore system

Google's recommends storing user biometric data using Android KeyStore system used by the KeyChain
API introduced in API level 14 (Android 4.0). This system reduces the risk of unauthorized use of
key material from outside the Android device by preventing the extraction of the key material from
application processes and from the Android device as a whole. Second, the Keystore system reduces
the risk of unauthorized use of key material within the Android device by making apps specify the
authorized uses of their keys and then enforcing those restrictions outside of the apps' processes.
Therefore, keys can be protected from extraction and unauthorized use, even if the application code
or the device’s operating system is compromised.

Android KeyStore main features are:

- Isolation: Keys are stored in a container that makes them inaccessible to other applications and
  the operating system
- Secure Key Operations: Cryptographic operations using the keys are performed within the Keystore,
  preventing direct access to the key material
- Hardware Protection: On devices with hardware support, keys can be stored and used within secure
  hardware modules

Since Google Pixel 3 (released in 2018), more and more devices with Android 9 support hardware
protection using StrongBox. Providing an added security level:

- Dedicated Hardware Security Module (HSM): Isolates keys from the main processor and TEE
- Tamper Resistance: Offers resistance against physical attacks and hardware tampering
- Enhanced Security Features: Supports features like ID attestation and anti-replay protections

![Android Keystore Architecture](docs%2Fandroid-keystore-arch.png)

## AndroidX Biometric library

AndroidX Biometric library provides the following benefits:

- A common authentication UI familiar for all users
- Compatibility with previous Android SDKs and future releases
- Simple methods to detect device capabilities regarding biometric authentication
- Freedom of determining how strong or weak a biometric authentication is
- When multiple biometric authentication methods are available, it will selected the method
  configured by the user

![AndroidX Biometric Architecture](docs%2Fandroidx-biometric-arch.png)

## References

- <a target="_blank" href="https://developer.android.com/identity/sign-in/biometric-auth">Show a
  biometric authentication dialog</a>
- <a target="_blank" href="https://medium.com/@en.mazzucchelli/biometric-authentication-jetpack-compose-146ee35e7039">
  Biometric Authentication & Jetpack ComposeBiometric Authentication & Jetpack Compose</a>
- <a target="_blank" href="https://medium.com/@pouryarezaee76/encrypt-data-store-android-444b57d04e7d">
  Encrypt Data Store Android</a>
- <a target="_blank" href="https://iamjosephmj.medium.com/biometric-encryption-and-decryption-with-androidx-biometric-13741a5b0583">
  Biometric Encryption and Decryption with AndroidX.Biometric library: Enhancing Security and User
  Experience</a>
- <a target="_blank" href="https://medium.com/androiddevelopers/using-biometricprompt-with-cryptoobject-how-and-why-aace500ccdb7">
  Using BiometricPrompt with CryptoObject: how and why</a> 
