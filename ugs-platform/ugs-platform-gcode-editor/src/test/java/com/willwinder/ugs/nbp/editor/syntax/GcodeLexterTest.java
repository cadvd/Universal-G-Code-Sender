/*
    Copyright 2016-2019 Will Winder

    This file is part of Universal Gcode Sender (UGS).

    UGS is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    UGS is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with UGS.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.willwinder.ugs.nbp.editor.syntax;

import com.willwinder.ugs.nbp.editor.lexer.GcodeTokenId;
import org.junit.Test;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;

import static org.junit.Assert.assertEquals;

public class GcodeLexterTest {
    @Test
    public void parsingGcodeShouldIdentifyInlineComments() {
        String text = "G21 ;inline comment 1";
        TokenSequence<GcodeTokenId> ts = parseTokenSequence(text);

        ts.moveNext();
        Token<?> t = ts.token();
        assertEquals(GcodeTokenId.MOVEMENT, t.id());
        assertEquals("G21", t.text());

        ts.moveNext();
        t = ts.token();
        assertEquals(GcodeTokenId.WHITESPACE, t.id());
        assertEquals(" ", t.text());

        ts.moveNext();
        t = ts.token();
        assertEquals(GcodeTokenId.COMMENT, t.id());
        assertEquals(";inline comment 1", t.text());
    }

    @SuppressWarnings("unchecked")
    private TokenSequence<GcodeTokenId> parseTokenSequence(String text) {
        TokenHierarchy<?> hi = TokenHierarchy.create(text, GcodeTokenId.getLanguage());
        return (TokenSequence<GcodeTokenId>) hi.tokenSequence();
    }

    @Test
    public void parsingGcodeShouldIdentifyComments() {
        String text = "G21 (comment 1)";
        TokenSequence<GcodeTokenId> ts = parseTokenSequence(text);

        ts.moveNext();
        Token<?> t = ts.token();
        assertEquals(GcodeTokenId.MOVEMENT, t.id());
        assertEquals("G21", t.text());

        ts.moveNext();
        t = ts.token();
        assertEquals(GcodeTokenId.WHITESPACE, t.id());
        assertEquals(" ", t.text());

        ts.moveNext();
        t = ts.token();
        assertEquals(GcodeTokenId.COMMENT, t.id());
        assertEquals("(comment 1)", t.text());
    }

    @Test
    public void parsingGcodeShouldIdentifyCommentsWithoutEndParanthesisAsAnError() {
        String text = "G21 (inline comment 1";
        TokenSequence<GcodeTokenId> ts = parseTokenSequence(text);

        ts.moveNext();
        Token<?> t = ts.token();
        assertEquals(GcodeTokenId.MOVEMENT, t.id());
        assertEquals("G21", t.text());

        ts.moveNext();
        t = ts.token();
        assertEquals(GcodeTokenId.WHITESPACE, t.id());
        assertEquals(" ", t.text());

        ts.moveNext();
        t = ts.token();
        assertEquals(GcodeTokenId.ERROR, t.id());
        assertEquals("(inline comment 1", t.text());
    }

    @Test
    public void parsingGcodeShouldIdentifyMultilineCommentsAsAnError() {
        String text = "G21 (inline comment 1\ncontinue)";
        TokenSequence<GcodeTokenId> ts = parseTokenSequence(text);

        ts.moveNext();
        Token<?> t = ts.token();
        assertEquals(GcodeTokenId.MOVEMENT, t.id());
        assertEquals("G21", t.text());

        ts.moveNext();
        t = ts.token();
        assertEquals(GcodeTokenId.WHITESPACE, t.id());
        assertEquals(" ", t.text());

        ts.moveNext();
        t = ts.token();
        assertEquals(GcodeTokenId.ERROR, t.id());
        assertEquals("(inline comment 1", t.text());
    }

    @Test
    public void parsingGcodeShouldIdentifyMachineCommands() {
        String text = "M100\nM200";
        TokenSequence<GcodeTokenId> ts = parseTokenSequence(text);

        ts.moveNext();
        Token<?> t = ts.token();
        assertEquals(GcodeTokenId.MACHINE, t.id());
        assertEquals("M100", t.text());

        ts.moveNext();
        t = ts.token();
        assertEquals(GcodeTokenId.WHITESPACE, t.id());
        assertEquals("\n", t.text());

        ts.moveNext();
        t = ts.token();
        assertEquals(GcodeTokenId.MACHINE, t.id());
        assertEquals("M200", t.text());
    }

    @Test
    public void parsingGcodeShouldIdentifyMovementCommands() {
        String text = "G01\nG02";
        TokenSequence<GcodeTokenId> ts = parseTokenSequence(text);

        ts.moveNext();
        Token<?> t = ts.token();
        assertEquals(GcodeTokenId.MOVEMENT, t.id());
        assertEquals("G01", t.text());

        ts.moveNext();
        t = ts.token();
        assertEquals(GcodeTokenId.WHITESPACE, t.id());
        assertEquals("\n", t.text());

        ts.moveNext();
        t = ts.token();
        assertEquals(GcodeTokenId.MOVEMENT, t.id());
        assertEquals("G02", t.text());
    }

    @Test
    public void parsingGcodeShouldIdentifyParameters() {
        String text = "G01 X100 S100";
        TokenSequence<GcodeTokenId> ts = parseTokenSequence(text);

        ts.moveNext();
        Token<?> t = ts.token();
        assertEquals(GcodeTokenId.MOVEMENT, t.id());
        assertEquals("G01", t.text());

        ts.moveNext();
        t = ts.token();
        assertEquals(GcodeTokenId.WHITESPACE, t.id());
        assertEquals(" ", t.text());

        ts.moveNext();
        t = ts.token();
        assertEquals(GcodeTokenId.AXIS, t.id());
        assertEquals("X100", t.text());

        ts.moveNext();
        t = ts.token();
        assertEquals(GcodeTokenId.WHITESPACE, t.id());
        assertEquals(" ", t.text());

        ts.moveNext();
        t = ts.token();
        assertEquals(GcodeTokenId.PARAMETER, t.id());
        assertEquals("S100", t.text());
    }

    @Test
    public void parsingGcodeShouldIdentifyParametersWithDecimals() {
        String text = "G01 X-100.1 S100.10";
        TokenSequence<GcodeTokenId> ts = parseTokenSequence(text);

        ts.moveNext();
        Token<?> t = ts.token();
        assertEquals(GcodeTokenId.MOVEMENT, t.id());
        assertEquals("G01", t.text());

        ts.moveNext();
        t = ts.token();
        assertEquals(GcodeTokenId.WHITESPACE, t.id());
        assertEquals(" ", t.text());

        ts.moveNext();
        t = ts.token();
        assertEquals(GcodeTokenId.AXIS, t.id());
        assertEquals("X-100.1", t.text());

        ts.moveNext();
        t = ts.token();
        assertEquals(GcodeTokenId.WHITESPACE, t.id());
        assertEquals(" ", t.text());

        ts.moveNext();
        t = ts.token();
        assertEquals(GcodeTokenId.PARAMETER, t.id());
        assertEquals("S100.10", t.text());
    }

    @Test
    public void parsingParametersWithMultipleDecimalsShouldBeAnError() {
        String text = "G01 X-.100.1 S.100.10";
        TokenSequence<GcodeTokenId> ts = parseTokenSequence(text);

        ts.moveNext();
        Token<?> t = ts.token();
        assertEquals(GcodeTokenId.MOVEMENT, t.id());
        assertEquals("G01", t.text());

        ts.moveNext();
        t = ts.token();
        assertEquals(GcodeTokenId.WHITESPACE, t.id());
        assertEquals(" ", t.text());

        ts.moveNext();
        t = ts.token();
        assertEquals(GcodeTokenId.ERROR, t.id());
        assertEquals("X-.100.1", t.text());

        ts.moveNext();
        t = ts.token();
        assertEquals(GcodeTokenId.WHITESPACE, t.id());
        assertEquals(" ", t.text());

        ts.moveNext();
        t = ts.token();
        assertEquals(GcodeTokenId.ERROR, t.id());
        assertEquals("S.100.10", t.text());
    }
}
