## JioFi JMR815 (6)

![JioFi 6](images/jiofi-6.jpeg)

### Apis without authentication

* http://jiofi.local.html/lte_ajax.cgi
* http://jiofi.local.html/lan_ajax.cgi
* http://jiofi.local.html/wan_info_ajax.cgi
* http://jiofi.local.html/Device_info_ajax.cgi

### Authenticated Apis

All these require cookie from the login.

#### SSID and password change form

Request URL: http://jiofi.local.html/Security_Mode_sv.cgi

Request Method: POST

##### Post data

- form_type: wifi_set
- token: 4053938597
- SSID_text: JioFi_105F34C
- channel: AUTO
- technology: bgn
- WMM_config: 1
- HSSID_config: 0
- Se_Encryption: wpa2
- wpa_key: l02maz7hlg
- wpa2_key: l02maz7hlg
- wpa_mixed_key: l02maz7hlg

##### Request Header

Content-Type: application/x-www-form-urlencoded
Cookie: ksession=8f8367efdf968a3d3e74014daf042a24
