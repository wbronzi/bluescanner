/*
 * ------------------------------------------------------------------------------
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Walter Bronzi [wbronzi@gmail.com], [walter.bronzi@uni.lu]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * ------------------------------------------------------------------------------
 */



package com.snt.bt.recon.util;

import android.annotation.SuppressLint;

import java.text.DateFormatSymbols;
import java.util.Locale;
import java.util.TimeZone;

public class UtcDateFormatter extends java.text.SimpleDateFormat {
    private static final long serialVersionUID = 1L;

    private static final String TIME_ZONE_STRING = "UTC";
    private static final TimeZone TIME_ZONE_UTC = TimeZone.getTimeZone(TIME_ZONE_STRING);

    @SuppressLint("SimpleDateFormat")
    public UtcDateFormatter(final String template) {
        super(template);
        super.setTimeZone(TIME_ZONE_UTC);
    }

    @SuppressLint("SimpleDateFormat")
    public UtcDateFormatter(final String template, final DateFormatSymbols symbols) {
        super(template, symbols);
        super.setTimeZone(TIME_ZONE_UTC);
    }

    public UtcDateFormatter(final String template, final Locale locale) {
        super(template, locale);
        super.setTimeZone(TIME_ZONE_UTC);
    }

    /*
     * This function will throw an UnsupportedOperationException.
     * You are not be able to change the TimeZone of this object
      *
      * (non-Javadoc)
     * @see java.text.DateFormat#setTimeZone(java.util.TimeZone)
     */
    @Override
    public void setTimeZone(final TimeZone timezone) {
        throw new UnsupportedOperationException("This SimpleDateFormat can only be in " + TIME_ZONE_STRING);
    }
}
