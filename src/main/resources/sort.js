// JavaScript Document
//var infodata=
//    [
//        {"title":"连载", "data":[
//            {"id":39039,"comic_id":18741,"chapter_name":"9","chapter_order":9,"chaptertype":0,"title":null,"sort":null,last_updatetime:1},
//            {"id":38993,"comic_id":18741,"chapter_name":"8","chapter_order":8,"chaptertype":0,"title":null,"sort":null,last_updatetime:1},
//            {"id":38992,"comic_id":18741,"chapter_name":"7","chapter_order":7,"chaptertype":0,"title":null,"sort":null,last_updatetime:1}
//        ]},
//        {"title":"单行本", "data":[
//            {"id":39039,"comic_id":18741,"chapter_name":"9","chapter_order":9,"chaptertype":0,"title":null,"sort":null,last_updatetime:1},
//            {"id":38993,"comic_id":18741,"chapter_name":"8","chapter_order":8,"chaptertype":0,"title":null,"sort":null,last_updatetime:1},
//            {"id":38992,"comic_id":18741,"chapter_name":"7","chapter_order":7,"chaptertype":0,"title":null,"sort":null,last_updatetime:1},
//            {"id":38991,"comic_id":18741,"chapter_name":"6","chapter_order":6,"chaptertype":0,"title":null,"sort":null,last_updatetime:1},
//            {"id":38991,"comic_id":18741,"chapter_name":"5","chapter_order":5,"chaptertype":0,"title":null,"sort":null,last_updatetime:1},
//            {"id":38991,"comic_id":18741,"chapter_name":"4","chapter_order":4,"chaptertype":0,"title":null,"sort":null,last_updatetime:1},
//            {"id":38991,"comic_id":18741,"chapter_name":"3","chapter_order":3,"chaptertype":0,"title":null,"sort":null,last_updatetime:1},
//            {"id":38991,"comic_id":18741,"chapter_name":"2","chapter_order":2,"chaptertype":0,"title":null,"sort":null,last_updatetime:1},
//            {"id":38991,"comic_id":18741,"chapter_name":"1","chapter_order":1,"chaptertype":0,"title":null,"sort":null,last_updatetime:1}
//
//        ]},
//        {"title":"单行本", "data":[
//            {"id":39039,"comic_id":18741,"chapter_name":"9","chapter_order":9,"chaptertype":0,"title":null,"sort":null,last_updatetime:1},
//            {"id":38993,"comic_id":18741,"chapter_name":"8","chapter_order":8,"chaptertype":0,"title":null,"sort":null,last_updatetime:1},
//            {"id":38992,"comic_id":18741,"chapter_name":"7","chapter_order":7,"chaptertype":0,"title":null,"sort":null,last_updatetime:1},
//            {"id":38991,"comic_id":18741,"chapter_name":"6","chapter_order":6,"chaptertype":0,"title":null,"sort":null,last_updatetime:1},
//            {"id":38991,"comic_id":18741,"chapter_name":"5","chapter_order":5,"chaptertype":0,"title":null,"sort":null,last_updatetime:1},
//            {"id":38991,"comic_id":18741,"chapter_name":"4","chapter_order":4,"chaptertype":0,"title":null,"sort":null,last_updatetime:1},
//            {"id":38991,"comic_id":18741,"chapter_name":"3","chapter_order":3,"chaptertype":0,"title":null,"sort":null,last_updatetime:1},
//            {"id":38991,"comic_id":18741,"chapter_name":"2","chapter_order":2,"chaptertype":0,"title":null,"sort":null,last_updatetime:1},
//            {"id":38991,"comic_id":18741,"chapter_name":"1","chapter_order":1,"chaptertype":0,"title":null,"sort":null,last_updatetime:1}
//        ]}
//    ];

var firstcharpetId ="";
var firstComicId ="";
var firstCharpetName = "" ;
var jsonData = '';
var sort = {
    //是否已经展开
    expanded:[],

    currSortType:'',
    needReverse:false,

    desc:function(json) {
        var def_chap = json.lastObject()['data'].lastObject().id;
        var def_comicId = json.lastObject()['data'].lastObject().comic_id;
        var def_charpetName = json.lastObject()['data'].lastObject().chapter_name;
        var comicId =json[0]['data'][0].comic_id;
        firstcharpetId = def_chap;
        firstComicId = def_comicId;
        firstCharpetName = def_charpetName;
        m_global.comicId = comicId;
        $(".asc").removeClass("cur");
        $(".desc").addClass("cur");

        var htmlStr = getRowDivHtmlStr(json, 'desc');
        if(htmlStr) {
            $("#list").html(htmlStr);
        }
        //m_global.character("Drama li span",5);
    },

    asc:function(json){

        $(".asc").addClass("cur");
        $(".desc").removeClass("cur");

        var htmlStr = getRowDivHtmlStr(json, 'asc');
        if(htmlStr) {
            $("#list").html(htmlStr);
        }
        //m_global.character("Drama li span",5);
    },

    expand:function(obj, index){
        sort.expanded[index] = 1;

        var tmpBool = sort.needReverse;
        sort.needReverse = false;

        var htmlStr = '';
        for (var i = 0; i < jsonData.length; i++) {
            var itemData = jsonData[i];
            htmlStr += getChaptersHtmlStr(itemData, i);
        }
        if(htmlStr) {
            $("#list").html(htmlStr);
        }
        //m_global.character("Drama li span",5);
        sort.needReverse = tmpBool;
    }
};

/**
 * 获取一类章节的div容器html字符串
 * @param jsonStr
 * @param sortType
 * @returns {*}
 */
function getRowDivHtmlStr(jsonStr, sortType) {
    if(sort.currSortType == sortType) {
        return false;
    }

    var htmlStr = '';
    for (var i = 0; i < jsonStr.length; i++) {
        var itemData = jsonStr[i];
        htmlStr += getChaptersHtmlStr(itemData, i);
    }

    sort.currSortType = sortType;
    sort.needReverse = true;

    return htmlStr;
}

/**
 * 获取分类下所有章节的html字符串
 * @param itemObj
 * @param rowIndex
 * @returns {string}
 */
function getChaptersHtmlStr(itemObj, rowIndex) {

    var htmlStr = '<div class="qjBar">';

    htmlStr += itemObj.title;
    htmlStr += '<span>' + itemObj.data.length + '个章节</span>';
    htmlStr += '</div>';

    htmlStr += '<ul class="Drama autoHeight">';
    var itemArr = itemObj.data;
    if(sort.needReverse){
        itemArr.reverse();
    }
    var maybeShowAddButton = itemArr.length > 11;

    //还未展开
    var isExpanded = sort.expanded[rowIndex];
    if(!isExpanded) {
        itemArr = itemArr.slice(0, 11);
    }
    for (var i = 0; i < itemArr.length; i++) {
        htmlStr += getChapterHtmlStr(itemArr[i]);
    }

    if(!isExpanded && maybeShowAddButton) {
        htmlStr += '<li class="add" onclick="sort.expand($(this),' + rowIndex + ')">...</li>';
    }
    htmlStr += '</ul></div>';

    return htmlStr;
}

//记录kookie
function chapterCookie(comicId,chapterId,kookiepage,charpetName,cover,title){
    var cookieData = new Date();
    var imgSrc;
    if($("#Cover").length>0){
        imgSrc = $("#Cover img").attr("src");
    }else{
        imgSrc = cover
    }
    var comicName;
    if($("#comicName").length>0){
        comicName = $("#comicName").text();
    }else{
        comicName = title;
    }

    if(localStorage.readHistory==undefined){
        var item_obj = {};
        item_obj[comicId] = chapterId;
        item_obj["comicId"] = comicId;//漫画id
        item_obj["chapterId"] = chapterId;//话id
        item_obj["comicName"] = comicName;//漫画名字
        item_obj["charpetName"] = charpetName;//话名字
        item_obj["cover"] = imgSrc;//漫画封面
        item_obj["page"] = kookiepage;//第几页
        item_obj["time"] =cookieData.Format('yyyy-MM-dd');//观看时间
        //$.cookie("read-history", JSON.stringify([item_obj]),{path:"/",expires: 99999});
        localStorage.readHistory = JSON.stringify([item_obj]);
    }else{
        var cookie_obj = $.parseJSON(localStorage.readHistory);
        var exist = false;
        for(var i=0;i<cookie_obj.length;i++) {
            var obj = cookie_obj[i];
            if(obj[comicId]) {
                obj[comicId] = chapterId;//漫画id
                obj["comicId"] = comicId;//漫画id
                obj["chapterId"] = chapterId;//漫画id
                obj["page"] = kookiepage;//漫画页数
                obj["charpetName"] = charpetName;//漫画标题
                obj["time"] = cookieData.Format('yyyy-MM-dd');//观看时间
                exist = true;
                break;
            }
        }
        if(!exist) {
            var item_obj = {};
            item_obj[comicId] = chapterId;
            item_obj["comicId"] = comicId;//漫画id
            item_obj["chapterId"] = chapterId;//漫画id
            item_obj["cover"] = imgSrc;//漫画封面
            item_obj["comicName"] = comicName;//漫画标题
            item_obj["charpetName"] = charpetName;
            item_obj["page"] = kookiepage;
            item_obj["time"] =cookieData.Format('yyyy-MM-dd');
            cookie_obj.push(item_obj);
        }
        //$.cookie("read-history", JSON.stringify(cookie_obj),{path:"/",expires: 99999});
        localStorage.readHistory = JSON.stringify(cookie_obj);
    }
}

/*
    *是否有记录
    *继续观看
*/

function isRead(){
    $.ajax({
        type: "get",
        url: "/introduction/watchState",
        data: "id=" + firstComicId,
        async:true,
        datatype :jsonData,
        success: function (data) {
            if(data.isVisited==0){
                $("#continusRead").html("开始观看");
                $("#continusRead").attr("href","/view/"+firstComicId+"/"+firstcharpetId+".html");
            }else{
                $("#continusRead").html("继续观看");
                $("#continusRead").attr("href","/view/"+data.current.comic_id+"/"+data.current.chapter_id+".html")
            }
        }
    });
    /*if(localStorage.readHistory) {
        var cookie_obj = $.parseJSON(localStorage.readHistory);
        for(var i=0;i<cookie_obj.length;i++){
            var key = cookie_obj[i]['comicId'];
            if(key==firstComicId){
                $("#continusRead").html("继续观看");
                break;
            }
        }
    }*/
}

/*function continusRead(){
    if(($("#Subscribe").attr("onclick"))=="unSubscribe("+firstComicId+")"){
        update_read_status(firstComicId);
    }
    if(localStorage.readHistory==undefined){
        location.href="/view/"+firstComicId+"/"+firstcharpetId+".html";
        chapterCookie(firstComicId,firstcharpetId,1,firstCharpetName);
    }else{
        *//*var cookie_obj = $.parseJSON(localStorage.readHistory);
        var keyarry=[];
        console.log(cookie_obj)
        for(var i=0;i<cookie_obj.length;i++){
            for(var key in cookie_obj[i]){
                keyarry.push(key);
                if($.inArray(firstComicId, keyarry)!=-1){
                    if(key==firstComicId){
                        var cookiecharpetId = cookie_obj[i][firstComicId];
                        location.href="/view/"+firstComicId+"/"+cookiecharpetId+".html"
                    }
                }else{
                    location.href="/view/"+firstComicId+"/"+firstcharpetId+".html";
                    chapterCookie(firstComicId,firstcharpetId,1,firstCharpetName)
                }
            }
        }
*//*
        var cookie_obj = $.parseJSON(localStorage.readHistory);
        for(var i=0;i<cookie_obj.length;i++){
            var key = cookie_obj[i]['comicId'];
            if(key==firstComicId){
                var cookiecharpetId = cookie_obj[i][firstComicId];
                location.href="/view/"+firstComicId+"/"+cookiecharpetId+".html";
                break;
            }else{
                location.href="/view/"+firstComicId+"/"+firstcharpetId+".html";
                chapterCookie(firstComicId,firstcharpetId,1,firstCharpetName)
            }
        }

    }
}*/

/**
 * 根据chapter数据结构返回对应的html字符串
 * @param chapter
 * @returns {string}
 */
/*' + url + '*/
function getChapterHtmlStr(chapter) {
    var url = '/view/' + chapter.comic_id + '/' + chapter.id + '.html';
    var cookieId=[chapter.comic_id,chapter.id];
    var htmlStr = '<li><a href="' + url + '" onclick="chapterCookie('
                   +chapter.comic_id+','+chapter.id+',1,\''
                   +chapter.chapter_name+'\')"><span>';
    htmlStr += chapter.chapter_name;
    htmlStr += '</a></span>';

    var date1 = new Date(chapter.last_updatetime * 1000);
    var date2 = new Date();
    if (date1.Format('yyyy-MM-dd') == date2.Format('yyyy-MM-dd')) {
        htmlStr += '<p class="new">NEW</p>';
    }

    htmlStr += '</li>';

    return htmlStr;
}


//添加订阅
function addSubscribe(subId){
    UserCookie();
    if(m_global.isLogin==true){
        var subScribeArry = JSON.parse(localStorage.mySubscribeData);
        var url="https://"+domain_name+"interface.dmzj.com/api/subscribe/add";
        T.ajaxJsonp(url,{sub_id:subId,sub_type:0}, function (data) {
            if(data.result==1000){
                var html = '';
                html += '<div class="layerIcon01"></div>';
                html += '<p>漫画订阅成功</p>';
                html += '<p class="opacity">当漫画更新时我们将第一时间提醒您</p>';
                html += '<a class="layerBtn" id=Cancel>知道了</a>';
                openwindow(html);
                var sub_id = {};
                sub_id["sub_id"] = subId;
                subScribeArry.push(sub_id);
                localStorage.mySubscribeData=JSON.stringify(subScribeArry);
                $("#Subscribe").html("取消订阅").attr("onclick","unSubscribe("+subId+")");
                $("#mysub_"+subId).html("取消订阅").attr("onclick","unSubscribe("+subId+")");
                $("#subject_"+subId).html("已订阅").attr("onclick","");
            }else if(data.result==809){
                var html = '';
                html += '<div class="layerIcon06"></div>';
                html += '<p>订阅失败</p>';
                html += '<p class="opacity">您已经订阅过了！</p>';
                html += '<a class="layerBtn" id=Cancel>确定</a>';
                openwindow(html);
                $("#Subscribe").html("取消订阅").attr("onclick","unSubscribe("+subId+")");
                var sub_id = {};
                sub_id["sub_id"] = subId;
                subScribeArry.push(sub_id);
                localStorage.mySubscribeData=JSON.stringify(subScribeArry);
            }
        },function(){
            console.log(data.msg);
        });
    }else{
        location.href="/login.html";
    }
}
function addSubscribeAll() {
    UserCookie();
    if (m_global.isLogin == true) {
        var subScribeArry = JSON.parse(localStorage.mySubscribeData);
        var url = "https://" + domain_name + "interface.dmzj.com/api/subscribe/addMulity";
        var subIdArry =[];
        for(var i=0 ;i<$(".itemBox").length;i++){
            var subIdList = {}
            var attrId = $(".itemBox").eq(i).attr('id');
            subIdList[attrId] =0;
            subIdArry.push(subIdList)
        }
        var subId_type = JSON.stringify(subIdArry);
        $.ajax({
            url: url,
            dataType: 'jsonp',
            type: "get",
            async:false,
            cache: false, //开始缓存 不然会出现_=随机数
            jsonp: 'callback',//回调函数的函数名,默认是callback
            jsonpCallback: 'success', //值随便设置,回调函数的内容,若不设置,会生成随机数就不能用缓存了
            data: {jsonData:subId_type},
            success: function(data) {
                if (data.result == 1000) {
                    for(subid_i=0;subid_i<data.comic_arr.length;subid_i++){
                        for(attr_id=0;attr_id<$(".itemBox").length;attr_id++){
                            var attr_subId = $(".itemBox").eq(attr_id).attr('id');
                            if(attr_subId==data.comic_arr[subid_i]){
                                var sub_id = {};
                                 sub_id["sub_id"] = attr_subId;
                                 subScribeArry.push(sub_id);
                                 localStorage.mySubscribeData = JSON.stringify(subScribeArry);
                                 $("#subject_"+attr_subId).html("已订阅");
                                 $("#subject_"+attr_subId).attr("onclick","");
                                 break;
                            }
                        }
                    }
                    var html = '';
                    html += '<div class="layerIcon01"></div>';
                    html += '<p>'+data.msg+'</p>';
                    html += '<p class="opacity">当漫画更新时我们将第一时间提醒您</p>';
                    html += '<a class="layerBtn" id=Cancel>知道了</a>';
                    openwindow(html);
                }
            }
        })

    } else {
        location.href = "/login.html";
    }
}




//初始化
function initIntroData (json) {
    jsonData = json;
    sort.desc(json);
    isRead();
    UserCookie();
    if(m_global.isLogin){
        isSubscribe(firstComicId);
    }

}


$(function(){
    //m_global.character("Drama li span",5);
    m_global.character("introName",8);
    //m_global.character("BarTit",10);
    //显示全部
    var cur_status = "less";
    var charNumbers = $(".txtDesc").text().length; //总字数
    var limit = 50; //显示字数
    if (charNumbers > limit) {
        var orgText = $(".txtDesc").text(); //原始文本
        var orgHeight = $(".txtDesc").height(); //原始高度
        var showText = orgText.substring(0, limit)+'...'; //最终显示的文本
        $(".txtDesc").html(showText);
        var contentHeight = $(".txtDesc").height(); //截取内容后的高度
        $(".openBtn,.txtDesc").click(function() {
            if (cur_status == "less") {
                $(".txtDesc").height(contentHeight).text(orgText).animate({
                    height: orgHeight
                });
                $(".openBtn").addClass('openBtnC');
                cur_status = "more";
            } else {
                $(".txtDesc").height(orgHeight).text(showText).animate({
                    height: contentHeight
                });
                $(".openBtn").removeClass('openBtnC');
                cur_status = "less";
            }
        });
    } else {
        $(".openBtn").css("background","#fff").css("height","10px");
    }

});

