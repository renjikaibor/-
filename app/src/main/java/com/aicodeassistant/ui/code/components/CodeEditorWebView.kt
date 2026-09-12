package com.aicodeassistant.ui.code.components

import android.content.Context
import android.util.AttributeSet
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.aicodeassistant.domain.model.FileItem
import com.aicodeassistant.domain.model.CodeLanguage
import com.aicodeassistant.utils.FileUtils
import org.json.JSONObject

@Composable
fun CodeEditorWebView(
    file: FileItem,
    onContentChange: (String) -> Unit,
    onCursorChange: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var webView by remember { mutableStateOf<CodeEditorWebView?>(null) }

    AndroidView(
        factory = { ctx ->
            CodeEditorWebView(ctx).apply {
                webView = this
                setupWebView()
                loadEditor(file)
            }
        },
        update = { view ->
            view.loadFile(file)
        },
        modifier = modifier.fillMaxSize()
    )

    LaunchedEffect(file.id) {
        webView?.loadFile(file)
    }

    DisposableEffect(webView) {
        onDispose {
            webView?.destroy()
        }
    }
}

class CodeEditorWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr) {

    private var currentFile: FileItem? = null
    private var onContentChangeCallback: ((String) -> Unit)? = null
    private var onCursorChangeCallback: ((Int, Int) -> Unit)? = null
    private var isInitialized = false

    private val editorHtml = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <style>
                * { margin: 0; padding: 0; box-sizing: border-box; }
                body { 
                    font-family: 'Monospace', monospace; 
                    background: #1e1e1e; 
                    color: #d4d4d4; 
                    height: 100vh; 
                    overflow: hidden; 
                }
                #editor { height: 100%; }
                .cm-editor { height: 100%; font-size: 14px; line-height: 1.5; }
                .cm-content { padding: 16px; }
                .cm-gutters { background: #252526; border-right: 1px solid #3e3e42; }
                .cm-lineNumbers { color: #858585; }
                .cm-activeLine { background: #2d2d2d; }
                .cm-selectionBackground { background: #264f78; }
                .cm-matchingBracket { outline: 1px solid #569cd6; }
            </style>
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/codemirror/6.0.1/codemirror.min.css">
            <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/6.0.1/codemirror.min.js"></script>
            <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/6.0.1/lang-python.min.js"></script>
            <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/6.0.1/lang-javascript.min.js"></script>
            <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/6.0.1/lang-typescript.min.js"></script>
            <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/6.0.1/lang-html.min.js"></script>
            <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/6.0.1/lang-css.min.js"></script>
            <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/6.0.1/lang-json.min.js"></script>
            <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/6.0.1/lang-markdown.min.js"></script>
            <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/6.0.1/lang-rust.min.js"></script>
            <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/6.0.1/lang-go.min.js"></script>
            <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/6.0.1/lang-cpp.min.js"></script>
            <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/6.0.1/lang-java.min.js"></script>
            <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/6.0.1/lang-sql.min.js"></script>
            <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/6.0.1/lang-yaml.min.js"></script>
            <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/6.0.1/lang-xml.min.js"></script>
        </head>
        <body>
            <div id="editor"></div>
            <script>
                var editor = null;
                var currentLanguage = 'text';
                
                function initializeEditor(content, language) {
                    if (editor) {
                        editor.destroy();
                    }
                    
                    var langSupport = getLanguageSupport(language);
                    
                    editor = new CodeMirror.EditorView({
                        doc: content,
                        extensions: [
                            CodeMirror.basicSetup,
                            langSupport,
                            CodeMirror.lineNumbers(),
                            CodeMirror.highlightActiveLineGutter(),
                            CodeMirror.EditorView.lineWrapping,
                            CodeMirror.EditorView.theme({
                                '&': { backgroundColor: '#1e1e1e', color: '#d4d4d4', height: '100%' },
                                '.cm-content': { padding: '16px' },
                                '.cm-gutters': { backgroundColor: '#252526', borderRight: '1px solid #3e3e42' },
                                '.cm-lineNumbers': { color: '#858585' },
                                '.cm-activeLine': { backgroundColor: '#2d2d2d' },
                                '.cm-selectionBackground': { backgroundColor: '#264f78' },
                                '.cm-matchingBracket': { outline: '1px solid #569cd6' },
                                '.cm-cursor': { borderLeftColor: '#ffffff' }
                            }),
                            CodeMirror.EditorView.updateListener.of(function(v) {
                                if (v.docChanged) {
                                    Android.onContentChange(editor.state.doc.toString());
                                }
                                if (v.selectionSet) {
                                    var pos = editor.state.selection.main.head;
                                    var line = editor.state.doc.lineAt(pos).number;
                                    var col = pos - editor.state.doc.lineAt(pos).from;
                                    Android.onCursorChange(line, col);
                                }
                            })
                        ],
                        parent: document.getElementById('editor')
                    });
                    
                    currentLanguage = language;
                    isInitialized = true;
                }
                
                function getLanguageSupport(lang) {
                    switch(lang) {
                        case 'python': return CodeMirror.python();
                        case 'javascript': return CodeMirror.javascript();
                        case 'typescript': return CodeMirror.typescript();
                        case 'html': return CodeMirror.html();
                        case 'css': return CodeMirror.css();
                        case 'json': return CodeMirror.json();
                        case 'markdown': return CodeMirror.markdown();
                        case 'rust': return CodeMirror.rust();
                        case 'go': return CodeMirror.go();
                        case 'cpp': return CodeMirror.cpp();
                        case 'java': return CodeMirror.java();
                        case 'sql': return CodeMirror.sql();
                        case 'yaml': return CodeMirror.yaml();
                        case 'xml': return CodeMirror.xml();
                        default: return [];
                    }
                }
                
                function setContent(content) {
                    if (editor) {
                        editor.dispatch({ changes: { from: 0, to: editor.state.doc.length, insert: content } });
                    }
                }
                
                function getContent() {
                    return editor ? editor.state.doc.toString() : '';
                }
                
                function setLanguage(lang) {
                    if (editor && lang !== currentLanguage) {
                        var content = getContent();
                        initializeEditor(content, lang);
                    }
                }
                
                var isInitialized = false;
            </script>
        </body>
        </html>
    """.trimIndent()

    fun setupWebView() {
        settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = true
            allowContentAccess = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            setRenderPriority(WebSettings.RenderPriority.HIGH)
            cacheMode = WebSettings.LOAD_NO_CACHE
        }

        webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (currentFile != null) {
                    loadFile(currentFile!!)
                }
            }
        }

        webChromeClient = WebChromeClient()

        addJavascriptInterface(JavaScriptInterface(), "Android")
    }

    fun loadFile(file: FileItem) {
        currentFile = file
        if (isInitialized) {
            val language = file.language?.id ?: "text"
            val content = file.content ?: ""
            evaluateJavascript("setContent(${\"\"\"$content\"\"\"})", null)
            evaluateJavascript("setLanguage('$language')", null)
        } else {
            loadDataWithBaseURL(null, editorHtml, "text/html", "UTF-8", null)
        }
    }

    fun setCallbacks(onContentChange: (String) -> Unit, onCursorChange: (Int, Int) -> Unit) {
        onContentChangeCallback = onContentChange
        onCursorChangeCallback = onCursorChange
    }

    inner class JavaScriptInterface {
        @JavascriptInterface
        fun onContentChange(content: String) {
            onContentChangeCallback?.invoke(content)
        }

        @JavascriptInterface
        fun onCursorChange(line: Int, col: Int) {
            onCursorChangeCallback?.invoke(line, col)
        }
    }

    companion object {
        fun create(context: Context): CodeEditorWebView {
            return CodeEditorWebView(context)
        }
    }
}
