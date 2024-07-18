package org.example.common;


public enum SearchDataType {
    /**
     * Int类型；
     */
    INTEGER {
        @Override
        public ElasticsearchType transformEs() {
            return ElasticsearchType.INTEGER;
        }

    },
    /**
     * 字符串
     */
    TEXT {
        @Override
        public ElasticsearchType transformEs() {
            return ElasticsearchType.TEXT;
        }

    },
    /**
     * 日期
     */
    DATE {
        @Override
        public ElasticsearchType transformEs() {
            return ElasticsearchType.DATE;
        }

    },
    /**
     * 日期时间
     */
    DATETIME {
        @Override
        public ElasticsearchType transformEs() {
            return ElasticsearchType.LONG;
        }

    },
    /**
     * char;
     */
    CHAR {
        @Override
        public ElasticsearchType transformEs() {
            return ElasticsearchType.KEYWORD;
        }

    },
    /**
     * 单浮点型
     */
    FLOAT {
        @Override
        public ElasticsearchType transformEs() {
            return ElasticsearchType.FLOAT;
        }

    },
    /**
     * 双浮点型
     */
    DECIMAL {
        @Override
        public ElasticsearchType transformEs() {
            return ElasticsearchType.DOUBLE;
        }

    },
    /**
     * 布尔类型
     */
    BOOL {
        @Override
        public ElasticsearchType transformEs() {
            return ElasticsearchType.BOOLEAN;
        }

    },
    /**
     * 大字段
     */
    CLOB {
        @Override
        public ElasticsearchType transformEs() {
            return ElasticsearchType.TEXT;
        }

    },
    /**
     * 二进制；
     */
    BLOB {
        @Override
        public ElasticsearchType transformEs() {
            return ElasticsearchType.BINARY;
        }

    },
    /**
     * 未一标识列；
     */
    UNIQUE {
        @Override
        public ElasticsearchType transformEs() {
            return ElasticsearchType.KEYWORD;
        }

    },
    /**
     * long
     */
    LONG {
        @Override
        public ElasticsearchType transformEs() {
            return ElasticsearchType.LONG;
        }

    },
    DOUBLE {
        @Override
        public ElasticsearchType transformEs() {
            return ElasticsearchType.DOUBLE;
        }


    },
    /**
     * 数组；
     */
    ARRAY,
    /**
     * 对象；
     */
    OBJECT;

    /**
     * 转换成
     *
     * @return
     */
    public ElasticsearchType transformEs() {
        throw new UnsupportedOperationException("无法进行类型转换；");
    }


}
