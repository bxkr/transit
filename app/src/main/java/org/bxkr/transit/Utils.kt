package org.bxkr.transit

import android.content.res.Resources

val Int.dp: Float get() = this * Resources.getSystem().displayMetrics.density