## JioFi JMR815 (6)

![JioFi 6](images/JioFi-images/jiofi6.jpg)

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


#### Mac filter (Block device)

Enable device block list

url: "/MAC_Filter_ajax.cgi?"+timestamp

```javascript
var timestamp = Number(new Date());
```

Response: { state:'success', token:'4564731487' } 

#### Turn off

Request URL: http://jiofi.local.html/MAC_Filter_ajax.cgi?1533475544302
Request Method: POST
Status Code: 200 OK
Remote Address: 192.168.15.1:80
Referrer Policy: no-referrer-when-downgrade

##### Post Data
- token: 5117341133
- mode: 0

#### Block one or more devices

Request URL: http://jiofi.local.html/MAC_Filter_ajax.cgi?1533474990415
Request Method: POST
Status Code: 200 OK
Remote Address: 192.168.15.1:80
Referrer Policy: no-referrer-when-downgrade

##### Post data for a single device

token: 5372034741
mode: 2
deny_cnt: 1
deny_0_mac: c4:8e:8f:01:5e:f5
deny_0_dis: android
deny_0_enable: checked
deny_0_key: 

##### Post data for multiple devices

token: 469468408
mode: 2
deny_cnt: 2
deny_0_mac: c4:8e:8f:01:5e:f5
deny_0_dis: android
deny_0_enable: checked
deny_0_key: 
deny_1_mac: c4:8e:8f:01:5e:f6
deny_1_dis: another
deny_1_enable: checked
deny_1_key: 
