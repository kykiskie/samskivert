//
// $Id: FormTool.java,v 1.12 2003/10/21 21:05:44 eric Exp $
//
// samskivert library - useful routines for java programs
// Copyright (C) 2001 Michael Bayne
//
// This library is free software; you can redistribute it and/or modify it
// under the terms of the GNU Lesser General Public License as published
// by the Free Software Foundation; either version 2.1 of the License, or
// (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

package com.samskivert.velocity;

import javax.servlet.http.HttpServletRequest;

import com.samskivert.servlet.util.HTMLUtil;
import com.samskivert.servlet.util.ParameterUtil;
import com.samskivert.util.StringUtil;

/**
 * The form tool can be placed into an invocation context and used by the
 * template to create form elements which automatically inherit form
 * values which were provided to the page at request time.
 *
 * For example:
 *
 * <pre>
 * Please enter your name: $form.text("name", 40) $form.submit("submit")
 * </pre>
 *
 * If the servlet was invoked with a value for "name", it will be
 * automatically filled into the "name" form field when it is requested.
 */
public class FormTool
{
    /**
     * Constructs a form tool that will use the supplied HTTP servlet
     * request object to prefetch form values.
     */
    public FormTool (HttpServletRequest req)
    {
        _req = req;
    }

    /**
     * Creates a text input field with the specified name and no extra
     * arguments or default value.
     *
     * @see #text(String,String,Object)
     */
    public String text (String name)
    {
        return input("text", name, "", "");
    }

    /**
     * Creates a text input field with the specified name and the
     * specified extra arguments with no default value.
     *
     * @see #text(String,String,Object)
     */
    public String text (String name, String extra)
    {
        return input("text", name, extra, "");
    }

    /**
     * Creates a text input field with the specified name, extra arguments
     * and default value. For example, a call to <code>text("foo",
     * "size=\"5\" maxLength=\"8\"", "bar")</code> would result in an
     * input field looking like:
     *
     * <pre>
     * &lt;input type="text" name="foo" value="bar" size="5" maxLength="8"&gt;
     * </pre>
     *
     * Assuming the <code>foo</code> parameter had no pre-existing value.
     */
    public String text (String name, String extra,
                        Object defaultValue)
    {
        return input("text", name, extra, defaultValue);
    }

    /**
     * Creates a password input field with the specified name and no extra
     * arguments or default value.
     *
     * @see #password(String,String,Object)
     */
    public String password (String name)
    {
        return input("password", name, "", "");
    }

    /**
     * Creates a password input field with the specified name and the
     * specified extra arguments with no default value.
     *
     * @see #password(String,String,Object)
     */
    public String password (String name, String extra)
    {
        return input("password", name, extra, "");
    }

    /**
     * Creates a password input field with the specified name, extra arguments
     * and default value. For example, a call to <code>password("foo",
     * "size=\"5\"", "bar")</code> would result in an
     * input field looking like:
     *
     * <pre>
     * &lt;input type="password" name="foo" value="bar" size="5"&gt;
     * </pre>
     *
     * Assuming the <code>foo</code> parameter had no pre-existing value.
     */
    public String password (String name, String extra, Object defaultValue)
    {
        return input("password", name, extra, defaultValue);
    }

    /**
     * Constructs a submit element with the name <code>submit</code> and
     * the specified button text.
     */
    public String submit (String text)
    {
        return fixedInput("submit", "submitBtn", text, "");
    }

    /**
     * Constructs a submit element with the name <code>submit</code> and
     * the specified button text with the specified extra text.
     */
    public String submitExtra (String text, String extra)
    {
        return fixedInput("submit", "submitBtn", text, extra);
    }

    /**
     * Constructs a submit element with the specified parameter name and
     * the specified button text.
     */
    public String submit (String name, String text)
    {
        return fixedInput("submit", name, text, "");
    }

    /**
     * Constructs a submit element with the specified parameter name and
     * the specified button text with the specified extra text.
     */
    public String submitExtra (String name, String text, String extra)
    {
        return fixedInput("submit", name, text, extra);
    }

    /**
     * Constructs a image submit element with the specified parameter name
     * and image path.
     */
    public String imageSubmit (String name, String imagePath)
    {
        return fixedInput("image", name, "", "src=\"" + imagePath + "\"");
    }

    /**
     * Constructs a image submit element with the specified parameter name
     * and image path.
     */
    public String imageSubmit (String name, String value, String imagePath)
    {
        return fixedInput("image", name, value, "src=\"" + imagePath + "\"");
    }

    /**
     * Constructs a image submit element with the specified parameter name
     * and image path.
     */
    public String imageSubmit (String name, String value, String imagePath,
                               String altText)
    {
        return fixedInput("image", name, value, "src=\"" + imagePath + "\" " +
                          "alt=\"" + altText + "\"");
    }

    /**
     * Constructs a button input element with the specified parameter name,
     * the specified button text, and the specified extra text.
     */
    public String button (String name, String text, String extra)
    {
        return fixedInput("button", name, text, extra);
    }

    /**
     * Constructs a hidden element with the specified parameter name where
     * the value is extracted from the appropriate request parameter.
     */
    public String hidden (String name)
    {
        return hidden(name, "");
    }

    /**
     * Constructs a hidden element with the specified parameter name where
     * the value is extracted from the appropriate request parameter
     * unless there is no value in which case the supplied default value
     * is used.
     */
    public String hidden (String name, Object defaultValue)
    {
        return fixedHidden(name, getValue(name, defaultValue));
    }

    /**
     * Constructs a fixed hidden element with the specified parameter name
     * and value. The contents of the parameter of the same name are
     * ignored when creating this element.
     */
    public String fixedHidden (String name, String value)
    {
        return fixedInput("hidden", name, value, "");
    }

    /**
     * Generates a hidden form field named <code>action</code> with the
     * specified value. This is handy when you have multiple forms
     * submitting to the same servlet and you need to distinguist the
     * action desired by the user.
     */
    public String action (String value)
    {
        return fixedInput("hidden", "action", value, "");
    }

    /**
     * Constructs a checkbox input field with the specified name and
     * default value.
     */
    public String checkbox (String name, boolean defaultValue)
    {
        StringBuffer buf = new StringBuffer();
        buf.append("<input type=\"checkbox\"");
        buf.append(" name=\"").append(name).append("\"");
        if (ParameterUtil.isSet(_req, name, defaultValue)) {
            buf.append(" checked");
        }
        buf.append(">");
        return buf.toString();
    }

    /**
     * Constructs an option entry for a select menu with the specified
     * name, value, item, and default selected value.
     */
    public String option (
        String name, String value, String item, Object defaultValue)
    {
        StringBuffer buf = new StringBuffer();
        buf.append("<option value=\"").append(value).append("\"");
        String selectedValue = getValue(name, defaultValue);
        if (selectedValue.equals(value)) {
            buf.append(" selected");
        }
        buf.append(">").append(item).append("</option>");
        return buf.toString();
    }

    /**
     * Creates a radio button with the specified name and value.
     */
    public String radio (String name, String value)
    {
        StringBuffer buf = new StringBuffer();
        buf.append("<input type=\"radio\"");
        buf.append(" name=\"").append(name).append("\"");
        buf.append(" value=\"").append(value).append("\"");
        String selectedValue = getValue(name, null);
        if (value.equals(selectedValue)) {
            buf.append(" checked");
        }
        buf.append(">");
        return buf.toString();
    }

    /**
     * Constructs a text area with the specified name, optional extra
     * parameters, and default text.
     */
    public String textarea (String name, String extra, Object defaultValue)
    {
        StringBuffer buf = new StringBuffer();
        buf.append("<textarea name=\"").append(name).append("\"");
        if (!StringUtil.blank(extra)) {
            buf.append(" ").append(extra);
        }
        buf.append(">");
        buf.append(getValue(name, defaultValue));
        buf.append("</textarea>");
        return buf.toString();
    }

    /**
     * Generates an input form field with the specified type, name,
     * defaultValue and extra attributes.
     */
    protected String input (String type, String name, String extra,
                            Object defaultValue)
    {
        return fixedInput(type, name, getValue(name, defaultValue), extra);
    }

    /**
     * Generates an input form field with the specified type, name, value
     * and extra attributes. The value is not fetched from the request
     * parameters but is always the value supplied.
     */
    protected String fixedInput (
        String type, String name, String value, String extra)
    {
        StringBuffer buf = new StringBuffer();
        buf.append("<input type=\"").append(type).append("\"");
        buf.append(" name=\"").append(name).append("\"");
        buf.append(" value=\"").append(value).append("\"");
        if (!StringUtil.blank(extra)) {
            buf.append(" ").append(extra);
        }
        buf.append(">");
        return buf.toString();
    }

    /**
     * Fetches the requested value from the servlet request and entifies
     * it appropriately.
     */
    protected String getValue (String name, Object defaultValue)
    {
        String value = ParameterUtil.getParameter(_req, name, true);
        if (StringUtil.blank(value)) {
            if (defaultValue == null) {
                value = "";
            } else {
                value = String.valueOf(defaultValue);
            }
        }
        return HTMLUtil.entify(value);
    }

    /** A reference to the servlet request in use by this form tool. */
    protected HttpServletRequest _req;
}
