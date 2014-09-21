Usage
=====

### Note :
This module is still under development and this document presently shows only currently available utilities.

#### IndicTextView
```

        <org.silpa.render.IndicTextView            
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:textColor="#000000"
            android:text="മലയാളം" />

```
The above XML script is used to create an IndicTextView.

#### IndicEditText
```

        <org.silpa.render.IndicEditText
            android:layout_width="match_parent"
            android:layout_height="wrap_content"            
            android:textColor="#000000"
            android:text="ગુજરાતી" />

```
The above XML script is used to create an IndicEditText.

#### Bitmap Rendering
```

        int width = ... ;        // The width of the bitmap   
        int height = ... ;       // The height of the bitmap
        int fontSize = ... ;     // Font Size of text required
        int color = ... ;        // Text color required
        String text = ... ;      // Text for rendering on bitmap
        
        Bitmap bitmap = scriptRenderer.getRenderedBitmap(text, fontSize,
            color, height, width);

```
The above code is used to render text on a Bitmap.


