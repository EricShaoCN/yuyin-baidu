(ns yuyin-baidu.token
  (:require [clj-http.client :as http]
            [clojure.data.json :as json]))

(def ^:private app-id "23501167")

(def ^:private api-key "zWvHOjlB3kGF47GGQbddBg18")

(def ^:private secret-key "jzo5Ris0x4nO0Pal6wzVQeF6HB2gIUpN")

(def access-token-url "https://aip.baidubce.com/oauth/2.0/token")

(defn get-access-token
  []
  (let [{:keys [status body]} (http/get access-token-url
                                        {:query-params
                                         {:grant_type "client_credentials" ;必须参数，固定为client_credentials；
                                          :client_id api-key ;必须参数，应用的API Key；
                                          :client_secret secret-key ;必须参数，应用的Secret Key；

                                          }})
        edn-body (json/read-str body :key-fn keyword)]

    (case status
      200 (let [{:keys [refresh_token expires_in session_key access_token scope session_secret]} edn-body] access_token)
      ;; 401 "可能是appkey appSecret 填错"
      ;; (let [{:keys [error error_description]} edn-body] error_description)
      nil)))

(comment
  (def response {:cached nil,
                 :request-time 540,
                 :repeatable? false,
                 :protocol-version {:name "HTTP",
                                    :major 1,
                                    :minor 1},
                 :streaming? true,
                 :http-client "";;#object[org.apache.http.impl.client.InternalHttpClient 0x36be4d8a "org.apache.http.impl.client.InternalHttpClient@36be4d8a"],
                 :chunked? true,
                 :cookies {"BAIDUID" {:discard false,
                                      :domain "baidu.com",
                                      :expires #inst "2037-12-31T23:55:55.000-00:00",
                                      :path "/",
                                      :secure false,
                                      :value "01B74D100ECA9337ABAF02BB55CDF78D:FG=1",
                                      :version 0}},
                 :reason-phrase "OK",
                 :headers {"Server" "Apache",
                           "Content-Type" "application/json",
                           "Connection" "close",
                           "Transfer-Encoding" "chunked",
                           "P3p" "CP=\" OTI DSP COR IVA OUR IND COM \"",
                           "Date" "Thu,
 07 Jan 2021 16:42:06 GMT",
                           "Vary" "Accept-Encoding",
                           "Cache-Control" "no-store"},
                 :orig-content-encoding nil,
                 :status 200,
                 :length -1,
                 :body "{\"refresh_token\":\"25.095f400d9fe7c46e11f1aa3fa5130883.315360000.1925397726.282335-23501167\",
\"expires_in\":2592000,
\"session_key\":\"9mzdA8gTrjJ7GV2\\/6RfBiebvrEgBsdIJ7J6QIFE09pVzCjUOy3uVXo4e0IiYnDwum6l8U\\/DJIJwEt6Tn2pMQy2UVfZcLAw==\",
\"access_token\":\"24.8be8b2bd5360012a30e698bb8cf461df.2592000.1612629726.282335-23501167\",
\"scope\":\"audio_voice_assistant_get brain_enhanced_asr audio_tts_post brain_speech_realtime public brain_all_scope picchain_test_picchain_api_scope brain_asr_async wise_adapt lebo_resource_base lightservice_public hetu_basic lightcms_map_poi kaidian_kaidian ApsMisTest_Test\\u6743\\u9650 vis-classify_flower lpq_\\u5f00\\u653e cop_helloScope ApsMis_fangdi_permission smartapp_snsapi_base smartapp_mapp_dev_manage iop_autocar oauth_tp_app smartapp_smart_game_openapi oauth_sessionkey smartapp_swanid_verify smartapp_opensource_openapi smartapp_opensource_recapi fake_face_detect_\\u5f00\\u653eScope vis-ocr_\\u865a\\u62df\\u4eba\\u7269\\u52a9\\u7406 idl-video_\\u865a\\u62df\\u4eba\\u7269\\u52a9\\u7406 smartapp_component smartapp_search_plugin\",
\"session_secret\":\"8087a2a9a798b2989426234fe139e12d\"}\n",
                 :trace-redirects []})
  )
