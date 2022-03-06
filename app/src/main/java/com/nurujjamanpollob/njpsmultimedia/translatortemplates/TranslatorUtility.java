package com.nurujjamanpollob.njpsmultimedia.translatortemplates;


/**
 * Class to build a URL that can be translated to a target Language
 * We're simply going to utilize Google Cloud Translation API, No plan is requires!
 */
public class TranslatorUtility {

    private String url;
    private final String languageCode;


    /**
     * Constructor Parameter of TranslatorUtility Class
     * @param url URl that needs to be translated.
     * @param languageCode a two alphabetic letter that specifies a country
     *                    or language code to translate the URL to this specific language.
     *                    For example bn to tell translator that translate content to Bengali Language.
     */
    public TranslatorUtility(String url, String languageCode) {
        this.url = url;
        this.languageCode = languageCode;
    }

    public void refactorQueryString(){

        StringBuilder sb = new StringBuilder();

        for (char c : url.toCharArray()){
            if ( c == '?'){
                url = sb.toString();
            }else {
                sb.append(c);
            }
        }

    }


    /**
     * Method to get translation URL, that can be used to show content in given Local Language Code
     * @return a URL that show content in given Local Language Code.
     */
    public String getGoogleTranslationURL() {

        // Google translator do not allow query String so lets refactor it
        refactorQueryString();

        StringBuilder r = new StringBuilder();
        int len = url.length();

        for(int i = 0; i < len; i++){
            char c = url.charAt(i);

            if (c == '/'){

                if (i + 1 < len && url.charAt(i + 1) == '/') {
                    r.append(c);
                    r.append(c);
                    // Move one shot forward
                    i++;
                }else {
                    return r + ".translate.goog" + url.substring(i, len) + "?_x_tr_sl=auto&_x_tr_tl=" + languageCode;
                }
            }
            else {

                if (c == '.'){
                    r.append('-');
                }else {
                    r.append(c);
                }
            }


        }

        return r.toString();
    }
}
