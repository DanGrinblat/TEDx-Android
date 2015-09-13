package com.example.dan.ted.TED.common;

/**
 * Created by Dan on 7/12/2015.
 */
public class Constants {
        //public static final String[] IMAGES = new String[] {
        //};

        private Constants() {
        }

        public static final String url = "http://10.0.3.2:5000/api/v1.0/";
        public static final String imageURL = "http://10.0.3.2:5000/api/v1.0/photo_gallery/";
        public static final String speakerURL = "http://10.0.3.2:5000/api/v1.0/event_details/speakers/";

        public static class Config {
            public static final boolean DEVELOPER_MODE = false;
        }

        public static class Extra {
            public static final String FRAGMENT_INDEX = "FRAGMENT_INDEX";
            public static final String IMAGE_POSITION = "IMAGE_POSITION";
        }
}
