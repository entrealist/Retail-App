package com.drugstore.app.core

import android.text.SpannableString
import android.text.Spanned
import android.text.style.URLSpan
import android.text.util.Linkify
import androidx.annotation.WorkerThread
import androidx.core.text.HtmlCompat
import androidx.core.text.getSpans
import androidx.core.text.util.LinkifyCompat

@WorkerThread
fun String.html(): Spanned = HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT)

@WorkerThread
fun String.linkifyHtml(): CharSequence {
    val html = HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT)
    val result = SpannableString(html)
    val resultSpans = result.getSpans<URLSpan>()

    val linkifiedHtml = SpannableString(html)
        .also { LinkifyCompat.addLinks(it, Linkify.ALL) }
    val linkifiedHtmlSpans = linkifiedHtml.getSpans<URLSpan>()

    linkifiedHtmlSpans
        .filterNot {
            val start = linkifiedHtml.getSpanStart(it)
            val end = linkifiedHtml.getSpanEnd(it)
            resultSpans.any { resultSpan ->
                val resultStart = result.getSpanStart(resultSpan)
                val resultEnd = result.getSpanEnd(resultSpan)
                start == resultStart && end == resultEnd
            }
        }
        .forEach {
            val start = linkifiedHtml.getSpanStart(it)
            val end = linkifiedHtml.getSpanEnd(it)
            result.setSpan(it, start, end, SpannableString.SPAN_INCLUSIVE_EXCLUSIVE)
        }

    return result
}