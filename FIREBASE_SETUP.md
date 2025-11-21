# Firebase Security Rules Setup

## Issues Found
1. ✅ Fixed: `TareasFragment` NullPointerException when accessing Toast context (committed to git)
2. ❌ **Firebase Storage returning 404** - No write permissions
3. ❌ **Firestore returning PERMISSION_DENIED** - No read/write permissions for tareas collection

## Solution: Update Firebase Security Rules

Go to **Firebase Console** → Your Project → **Firestore Database** → **Rules** tab and replace ALL content with:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Allow authenticated users to read/write their own user document
    match /users/{userId} {
      allow read, write: if request.auth.uid == userId;
      
      // Allow authenticated users to read/write their own tareas subcollection
      match /tareas/{document=**} {
        allow read, write: if request.auth.uid == userId;
      }
    }
  }
}
```

## Firebase Storage Rules

Go to **Firebase Console** → Your Project → **Storage** → **Rules** tab and replace with:

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    // Allow authenticated users to upload and download their own profile images
    match /profiles/{userId}/{allPaths=**} {
      allow read, write: if request.auth.uid == userId;
    }
    
    // Allow anyone to read public content
    match /public/{allPaths=**} {
      allow read;
    }
  }
}
```

## Steps to Apply

1. **Firestore Rules**:
   - Open Firebase Console
   - Select your project (lab520206311)
   - Click "Firestore Database"
   - Click "Rules" tab
   - Clear existing rules
   - Paste the Firestore rules above
   - Click "Publish"

2. **Storage Rules**:
   - Open Firebase Console
   - Select your project
   - Click "Storage"
   - Click "Rules" tab
   - Clear existing rules
   - Paste the Storage rules above
   - Click "Publish"

3. **Test**:
   - Rebuild and reinstall the app: `./gradlew.bat clean assembleDebug`
   - Try registering with new credentials (DNI/email)
   - Navigate to Profile
   - Select and upload an image
   - Verify the image loads and Firestore is updated

## Code Changes Made

### TareasFragment.java
- Added `import android.content.Context;`
- All `Toast.makeText(getContext(), ...)` calls now check for null context first:
  ```java
  Context ctx = getContext();
  if (ctx != null) {
      Toast.makeText(ctx, "message", Toast.LENGTH_SHORT).show();
  }
  ```
- This prevents NullPointerException when the fragment is detached during async operations

## Expected Results After Rules Update

✅ Firestore write operations: Users can write to their `users/{uid}` document and `tareas` subcollection
✅ Firebase Storage uploads: Users can upload profile images to `profiles/{uid}/` path
✅ TareasFragment: No more crashes when accessing Toast during fragment lifecycle

## Troubleshooting

If still getting permission errors:
- Verify user is properly authenticated (check Firebase Console → Authentication tab)
- Check that the user document exists in Firestore `users/{uid}` collection
- Ensure no old rules are interfering (check Rules history)
- Test from Firebase Console using "Test Rule" feature with your authenticated user UID
