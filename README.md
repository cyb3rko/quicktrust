# QuickTrust

[![API](https://img.shields.io/badge/API-23%2B-coral?logo=android&logoColor=white)](https://apilevels.com)
[![fdroid](https://img.shields.io/f-droid/v/de.cyb3rko.quicktrust.svg?logo=fdroid)](https://f-droid.org/packages/de.cyb3rko.quicktrust)
[![release](https://img.shields.io/github/release/cyb3rko/quicktrust.svg?logo=github)](https://github.com/cyb3rko/quicktrust/releases/latest)
[![last commit](https://img.shields.io/github/last-commit/cyb3rko/quicktrust?color=FE5196&logo=git&logoColor=white)](https://github.com/cyb3rko/quicktrust/commits/main)
[![license](https://img.shields.io/github/license/cyb3rko/quicktrust?color=1BCC1B&logo=apache)](https://www.apache.org/licenses/LICENSE-2.0)

<p align="center">
  <img alt="QuickTrust" src="https://i.imgur.com/gjIbTNi.png" width="150"/>
</p>

---

- [About this project](#about-this-project)   
- [Features](#features)  
- [Download](#download)
  - [Verification](#verification)
- [Badge for project reference](#badge-for-project-reference)
- [Screenshots](#screenshots)
- [Contribute](#contribute)
- [Used Icons](#used-icons)
- [License](#license)

---

## About this project
At some point I've wondered if there isn't a easier solution for verifying an app's signature.  
Current solutions are not how I imagined the process to be, so I've developed this app.  
Hope you like it. :)

Signature extraction inspired by [MuntashirAkon/AppManager](https://github.com/MuntashirAkon/AppManager) 💛  

## Features
- ✔️ Verification of app signatures via hash or hashfile
- 📲 Signature information of installed apps
- 📂 Signature information of APK files
- 🔒 private, no ads, no internet connection
- 💯 modern Material 3 design elements

## Download

**Google Play link coming soon**

**F-Droid link coming soon**

[<img height="80" src="https://github.com/machiav3lli/oandbackupx/blob/034b226cea5c1b30eb4f6a6f313e4dadcbb0ece4/badge_github.png"/>](https://github.com/cyb3rko/quicktrust/releases/latest)

### Verification

The APK files can be verified using [apksigner](https://developer.android.com/studio/command-line/apksigner.html#options-verify).

```
apksigner verify --print-certs -v example.apk
```

---

**F-Droid and GitHub** (signed using the same key)    
The output should look like this:

```
Verifies
Verified using v1 scheme (JAR signing): true
Verified using v2 scheme (APK Signature Scheme v2): true
```

The certificate content and digests should look like this:

```
DN: OU=QuickTrust, O=Cyb3rKo OpenSource, L=GitHub / F-Droid, C=DE
Certificate Digests:
  SHA-256: c6:1d:51:8b:eb:a7:c0:e7:fb:68:59:e0:6b:05:e0:7b:aa:05:a3:88:86:7c:8e:95:ee:f4:fc:bf:02:c6:74:c3
  SHA-1:   6a:62:b2:a5:af:50:85:2c:2b:f3:e3:a0:5c:81:7c:f7:dd:e3:72:32
  MD5:     0d:94:e9:b8:60:23:2b:49:62:5e:2a:8b:fc:0d:da:db
```

## Badge for project reference

If you want to recommend this app to your app users to verify it, you can use this badge in your README.

[![QuickTrust](https://img.shields.io/badge/verify-with%20QuickTrust-4DB45B)](https://github.com/cyb3rko/quicktrust)

```
[![QuickTrust](https://img.shields.io/badge/verify-with%20QuickTrust-4DB45B)](https://github.com/cyb3rko/quicktrust)
```

## Screenshots
|<img src="https://i.imgur.com/nJgaK37.png" width="350">|<img src="https://i.imgur.com/P7P7j8F.png" width="350">|
|:---:|:---:|

## Contribute
Of course I'm happy about any kind of contribution.

For creating [issues](https://github.com/cyb3rko/quicktrust/issues) there's no real guideline you should follow.
If you create [pull requests](https://github.com/cyb3rko/quicktrust/pulls) please try to use the syntax I use.
Using a unified code format makes it much easier for me and for everyone else.

## Used Icons

| 💛 |
| --- |  
| <a href="https://www.flaticon.com/free-icons/branch" title="branch icons">Branch icons created by Creatype - Flaticon</a> |
| <a href="https://www.flaticon.com/free-icons/information" title="information icons">Information icons created by Freepik - Flaticon</a> |
| <a href="https://www.flaticon.com/free-icons/upload" title="upload icons">Upload icons created by Ilham Fitrotul Hayat - Flaticon</a> |
| <a href="https://www.flaticon.com/free-icons/apk" title="apk icons">Apk icons created by juicy_fish - Flaticon</a> |
| <a href="https://www.flaticon.com/free-icons/more" title="more icons">More icons created by Ali Hasan Ash Shiddiq - Flaticon</a>  |

## License

    Copyright 2023-2024, Cyb3rKo

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
