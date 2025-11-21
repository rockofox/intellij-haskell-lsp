package boo.fox.haskelllsp.language

import com.intellij.lexer.LexerBase
import com.intellij.psi.tree.IElementType
import com.intellij.psi.TokenType

class LiterateHaskellLexerAdapter(private val baseLexer: HaskellLexerAdapter) : LexerBase() {
    private var buffer: CharSequence? = null
    private var bufferEnd = 0
    private var currentPos = 0
    private var tokenStart = 0
    private var tokenEnd = 0
    private var tokenType: IElementType? = null
    private var inCodeLine = false
    private var codeLineStart = 0
    
    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        this.buffer = buffer
        this.bufferEnd = endOffset
        this.currentPos = startOffset
        this.tokenStart = startOffset
        this.tokenEnd = startOffset
        this.tokenType = null
        this.inCodeLine = false
        this.codeLineStart = 0
        

        advance()
    }
    
    override fun advance() {
        if (currentPos >= bufferEnd) {
            tokenType = null
            return
        }
        

        var lineStart = currentPos
        while (lineStart > 0 && buffer!![lineStart - 1] != '\n' && buffer!![lineStart - 1] != '\r') {
            lineStart--
        }
        

        val isCodeLine = lineStart < bufferEnd &&
                buffer!![lineStart] == '>' &&
                (lineStart + 1 >= bufferEnd || buffer!![lineStart + 1] == ' ')
        
        if (isCodeLine) {

            if (!inCodeLine || codeLineStart != lineStart) {

                inCodeLine = true
                codeLineStart = lineStart
                val codeStart = lineStart + 2
                var lineEnd = codeStart
                while (lineEnd < bufferEnd && buffer!![lineEnd] != '\n' && buffer!![lineEnd] != '\r') {
                    lineEnd++
                }
                baseLexer.start(buffer!!, codeStart, lineEnd, 0)
            }
            
            if (baseLexer.tokenType != null) {
                tokenStart = baseLexer.tokenStart
                tokenEnd = baseLexer.tokenEnd
                tokenType = baseLexer.tokenType
                baseLexer.advance()
                

                var lineEnd = tokenEnd
                while (lineEnd < bufferEnd && buffer!![lineEnd] != '\n' && buffer!![lineEnd] != '\r') {
                    lineEnd++
                }
                
                if (baseLexer.tokenType == null || baseLexer.tokenStart >= lineEnd) {

                    currentPos = lineEnd

                    if (currentPos < bufferEnd && buffer!![currentPos] == '\r') currentPos++
                    if (currentPos < bufferEnd && buffer!![currentPos] == '\n') currentPos++
                    inCodeLine = false
                } else {
                    currentPos = tokenEnd
                }
            } else {

                var lineEnd = currentPos
                while (lineEnd < bufferEnd && buffer!![lineEnd] != '\n' && buffer!![lineEnd] != '\r') {
                    lineEnd++
                }
                currentPos = lineEnd

                if (currentPos < bufferEnd && buffer!![currentPos] == '\r') currentPos++
                if (currentPos < bufferEnd && buffer!![currentPos] == '\n') currentPos++
                inCodeLine = false
                advance()
            }
        } else {

            var lineEnd = currentPos
            while (lineEnd < bufferEnd && buffer!![lineEnd] != '\n' && buffer!![lineEnd] != '\r') {
                lineEnd++
            }
            
            tokenStart = currentPos
            tokenEnd = lineEnd
            tokenType = TokenType.WHITE_SPACE
            
            currentPos = lineEnd

            if (currentPos < bufferEnd && buffer!![currentPos] == '\r') currentPos++
            if (currentPos < bufferEnd && buffer!![currentPos] == '\n') currentPos++
            inCodeLine = false
        }
    }
    
    override fun getTokenType(): IElementType? = tokenType
    
    override fun getTokenStart(): Int = tokenStart
    
    override fun getTokenEnd(): Int = tokenEnd
    
    override fun getBufferSequence(): CharSequence = buffer ?: ""
    
    override fun getBufferEnd(): Int = bufferEnd
    
    override fun getState(): Int = 0
}
