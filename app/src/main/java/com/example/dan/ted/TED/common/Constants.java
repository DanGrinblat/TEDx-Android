package com.example.dan.ted.TED.common;

/**
 * Created by Dan on 7/12/2015.
 */
public class Constants {
        //public static final String[] IMAGES = new String[] {
        //};

        private Constants() {
        }
        public static final String baseUrl = "https://tedxcsu.pythonanywhere.com"; //https://tedxcsu.pythonanywhere.com/
        public static final String url = baseUrl + "/api/v1.0/";
        public static final String imageURL = url + "/photo_gallery/";
        public static final String speakerURL = url + "/event_details/speakers/";
        public static Long timestampLong = null;

        public static class Config {
            public static final boolean DEVELOPER_MODE = false;
        }

        public static class Extra {
            public static final String FRAGMENT_INDEX = "FRAGMENT_INDEX";
            public static final String IMAGE_POSITION = "IMAGE_POSITION";
        }
}
