/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dong.lan.tuyi.utils;

import android.content.Context;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.style.ImageSpan;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dong.lan.tuyi.R;


public class SmileUtils {


	public static final String u1 = "[u1]";
	public static final String u2 = "[u2]";
	public static final String u3 = "[u3]";
	public static final String u4 = "[u4]";
	public static final String u5 = "[u5]";
	public static final String u6 = "[u6]";
	public static final String u7 = "[u7]";
	public static final String u8 = "[u8]";
	public static final String u9 = "[u9]";
	public static final String u10 = "[u10]";
	public static final String u11 = "[u11]";
	public static final String u12 = "[u12]";
	public static final String u13 = "[u13]";
	public static final String u14 = "[u14]";
	public static final String u15 = "[u15]";
	public static final String u16 = "[u16]";
	public static final String u17 = "[u17]";
	public static final String u18 = "[u18]";
	public static final String u19 = "[u19]";
	public static final String u20 = "[u20]";
	public static final String u21 = "[u21]";
	public static final String u22 = "[u22]";
	public static final String u23 = "[u23]";
	public static final String u24 = "[u24]";
	public static final String u25 = "[u25]";
	public static final String u26 = "[u26]";
	public static final String u27 = "[u27]";
	public static final String u28 = "[u28]";
	public static final String u29 = "[u29]";
	public static final String u30 = "[u30]";
	public static final String u31 = "[u31]";
	public static final String u32 = "[u32]";
	public static final String u33 = "[u33]";
	public static final String u34 = "[u34]";
	public static final String u35 = "[u35]";
	public static final String u36 = "[u36]";
	public static final String u37 = "[u37]";
	public static final String u38 = "[u38]";
	public static final String u39 = "[u39]";
	public static final String u40 = "[u40]";
	public static final String u41 = "[u41]";
	public static final String u42 = "[u42]";
	public static final String u43 = "[u43]";
	public static final String u44 = "[u44]";
	public static final String u45 = "[u45]";
	public static final String u46 = "[u46]";
	public static final String u47 = "[u47]";
	public static final String u48 = "[u48]";
	public static final String u49 = "[u49]";
	public static final String u50 = "[u50]";
	public static final String u51 = "[u51]";
	public static final String u52 = "[u52]";
	public static final String u53 = "[u53]";
	public static final String u54 = "[u54]";
	public static final String u55 = "[u55]";
	public static final String u56 = "[u56]";
	public static final String u57 = "[u57]";
	public static final String u58 = "[u58]";
	public static final String u59 = "[u59]";
	public static final String u60 = "[u60]";
	public static final String u61 = "[u61]";
	public static final String u62 = "[u62]";
	public static final String u63 = "[u63]";
	public static final String u64 = "[u64]";
	public static final String u65 = "[u65]";
	public static final String u66 = "[u66]";
	public static final String u67 = "[u67]";
	public static final String u68 = "[u68]";
	public static final String u69 = "[u69]";
	public static final String u70 = "[u70]";





	private static final Factory spannableFactory = Factory
	        .getInstance();
	
	private static final Map<Pattern, Integer> emoticons = new HashMap<Pattern, Integer>();

	static {

		addPattern(emoticons,u1, R.drawable.u1);
		addPattern(emoticons,u2, R.drawable.u2);
		addPattern(emoticons,u3, R.drawable.u3);
		addPattern(emoticons,u4, R.drawable.u4);
		addPattern(emoticons,u5, R.drawable.u5);
		addPattern(emoticons,u6, R.drawable.u6);
		addPattern(emoticons,u7, R.drawable.u7);
		addPattern(emoticons,u8, R.drawable.u8);
		addPattern(emoticons,u9, R.drawable.u9);
		addPattern(emoticons,u10, R.drawable.u10);
		addPattern(emoticons,u11, R.drawable.u11);
		addPattern(emoticons,u12, R.drawable.u12);
		addPattern(emoticons,u13, R.drawable.u13);
		addPattern(emoticons,u14, R.drawable.u14);
		addPattern(emoticons,u15, R.drawable.u15);
		addPattern(emoticons,u16, R.drawable.u16);
		addPattern(emoticons,u17, R.drawable.u17);
		addPattern(emoticons,u18, R.drawable.u18);
		addPattern(emoticons,u19, R.drawable.u19);
		addPattern(emoticons,u20, R.drawable.u20);
		addPattern(emoticons,u21, R.drawable.u21);
		addPattern(emoticons,u22, R.drawable.u22);
		addPattern(emoticons,u23, R.drawable.u23);
		addPattern(emoticons,u24, R.drawable.u24);
		addPattern(emoticons,u25, R.drawable.u25);
		addPattern(emoticons,u26, R.drawable.u26);
		addPattern(emoticons,u27, R.drawable.u27);
		addPattern(emoticons,u28, R.drawable.u28);
		addPattern(emoticons,u29, R.drawable.u29);
		addPattern(emoticons,u30, R.drawable.u30);
		addPattern(emoticons,u31, R.drawable.u31);
		addPattern(emoticons,u32, R.drawable.u32);
		addPattern(emoticons,u33, R.drawable.u33);
		addPattern(emoticons,u34, R.drawable.u34);
		addPattern(emoticons,u35, R.drawable.u35);
		addPattern(emoticons,u36, R.drawable.u36);
		addPattern(emoticons,u37, R.drawable.u37);
		addPattern(emoticons,u38, R.drawable.u38);
		addPattern(emoticons,u39, R.drawable.u39);
		addPattern(emoticons,u40, R.drawable.u40);
		addPattern(emoticons,u41, R.drawable.u41);
		addPattern(emoticons,u42, R.drawable.u42);
		addPattern(emoticons,u43, R.drawable.u43);
		addPattern(emoticons,u44, R.drawable.u44);
		addPattern(emoticons,u45, R.drawable.u45);
		addPattern(emoticons,u46, R.drawable.u46);
		addPattern(emoticons,u47, R.drawable.u47);
		addPattern(emoticons,u48, R.drawable.u48);
		addPattern(emoticons,u49, R.drawable.u49);
		addPattern(emoticons,u50, R.drawable.u50);
		addPattern(emoticons,u51, R.drawable.u51);
		addPattern(emoticons,u52, R.drawable.u52);
		addPattern(emoticons,u53, R.drawable.u53);
		addPattern(emoticons,u54, R.drawable.u54);
		addPattern(emoticons,u55, R.drawable.u55);
		addPattern(emoticons,u56, R.drawable.u56);
		addPattern(emoticons,u57, R.drawable.u57);
		addPattern(emoticons,u58, R.drawable.u58);
		addPattern(emoticons,u59, R.drawable.u59);
		addPattern(emoticons,u60, R.drawable.u60);
		addPattern(emoticons,u61, R.drawable.u61);
		addPattern(emoticons,u62, R.drawable.u62);
		addPattern(emoticons,u63, R.drawable.u63);
		addPattern(emoticons,u64, R.drawable.u64);
		addPattern(emoticons,u65, R.drawable.u65);
		addPattern(emoticons,u66, R.drawable.u66);
		addPattern(emoticons,u67, R.drawable.u67);
		addPattern(emoticons,u68, R.drawable.u68);
		addPattern(emoticons,u69, R.drawable.u69);
		addPattern(emoticons,u70, R.drawable.u70);


	}

	private SmileUtils() {
	}

	private static void addPattern(Map<Pattern, Integer> map, String smile,
	        int resource) {
	    map.put(Pattern.compile(Pattern.quote(smile)), resource);
	}

	/**
	 * replace existing spannable with smiles
	 * @param context
	 * @param spannable
	 * @return
	 */
	public static boolean addSmiles(Context context, Spannable spannable) {
	    boolean hasChanges = false;
	    for (Entry<Pattern, Integer> entry : emoticons.entrySet()) {
	        Matcher matcher = entry.getKey().matcher(spannable);
	        while (matcher.find()) {
	            boolean set = true;
	            for (ImageSpan span : spannable.getSpans(matcher.start(),
	                    matcher.end(), ImageSpan.class))
	                if (spannable.getSpanStart(span) >= matcher.start()
	                        && spannable.getSpanEnd(span) <= matcher.end())
	                    spannable.removeSpan(span);
	                else {
	                    set = false;
	                    break;
	                }
	            if (set) {
	                hasChanges = true;
	                spannable.setSpan(new ImageSpan(context, entry.getValue()),
	                        matcher.start(), matcher.end(),
	                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	            }
	        }
	    }
	    return hasChanges;
	}

	public static Spannable getSmiledText(Context context, CharSequence text) {
	    Spannable spannable = spannableFactory.newSpannable(text);
	    addSmiles(context, spannable);
	    return spannable;
	}
	
	public static boolean containsKey(String key){
		boolean b = false;
		for (Entry<Pattern, Integer> entry : emoticons.entrySet()) {
	        Matcher matcher = entry.getKey().matcher(key);
	        if (matcher.find()) {
	        	b = true;
	        	break;
	        }
		}
		
		return b;
	}
	
	
	
}
