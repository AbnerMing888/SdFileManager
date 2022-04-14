$(function(){

	var items = ["/"];
            var mTempPath="";
			$(function() {

			    //获取分辨率的高
			    $(".fileDivHeight").css("height",(window.screen.height-280)+"px");

			    $(".deleteFile").click(function(){
			    //删除
			     var inputPath=$(".deleteInput").val();
			       if (inputPath.indexOf(".") != -1) {
                        //执行删除
                        $.post("file/deleteFile",{
                            path:inputPath
                        },function (data,status){
                             if(data.errorCode==200){
                                    $(".deleteInput").val("");
                                    alert("删除成功，刷新后可查看");
                                }
                        });
                   }else{
                        alert("请输入文件名");
                   }
			});

			    //上传文件
                     $(".btn").click(function(){
                     var tempFile=$("#upfile")[0].files[0];
                     if(tempFile==null||tempFile==""){
                        alert("请上传文件");
                        return;
                     }
                     if(mTempPath==null||mTempPath==""){
                           mTempPath="/-1/";
                     }
                     mTempPath=mTempPath.substring(1,mTempPath.length-1);
                        var formData = new FormData();
                        formData.append("path", mTempPath);
                        formData.append("file", tempFile);
                        $.ajax({
                            url: 'file/upload',
                            data: formData,
                            type: "post",
                            dataType: 'json',
                            cache : false,
                            contentType : false,
                            processData : false,
                            success: function(data) {
                                if(data.errorCode==200){
                                    alert("上传成功，刷新后可查看");
                                }
                            },
                            error:function () {
                                console.log("获取失败")
                            }
                        })
            });


				loadTimeData.data = {
					"header": "路径：LOCATION",
					"headerDateModified": "修改日期",
					"headerName": "名称",
					"headerSize": "大小",
					"language": "zh",
					"parentDirText": "[上级目录]",
					"textdirection": "ltr"
				};
				start("/");
				initFileList("", null);
				$("#parentDirLink").click(function() {
					if(items.length == 1) {
						return;
					}
					items.pop(); //出栈
					if(items.length == 1) {
						initFileList("", null)
						setFileTitleIndex();
						var box = document.getElementById("parentDirLinkBox");
						box.style.display = "hidden";
					} else {
						var path = setFileTitleIndex();
						initFileList("/storage/emulated/0" + path, null);
					}
				});

			});

			function setFileTitleIndex() {
				var pathStr = "";
				for(var i = 0; i < items.length; i++) {
					pathStr += items[i];
				}

				mTempPath=pathStr;//设置路径
				document.getElementById("header").innerText ="路径："+pathStr;
				document.getElementById("title").innerText = "路径："+pathStr;
				return pathStr;
			}

			function initFileList(rootPath, indexName) {
				console.info("rootPath:" + rootPath);
				if(rootPath==null||rootPath==""){
				     $(".deleteInput").val("/storage/emulated/0/");
				}else{
				    $(".deleteInput").val(rootPath);
				}

				$.ajax({
					type: "get",
					dataType: 'json', //服务器返回json格式数据
					timeout: 30000, //超时时间设置为30秒
					contentType: "application/json;charset=UTF-8",
					url: "/file/list?rootPath=" + rootPath,
					success: function(data) {
						var tbody = document.getElementById("tbody");
						tbody.innerHTML = "";
						if(data.data && data.data.length > 0) {
							for(var i = 0; i < data.data.length; i++) {
								var obj = data.data[i];
								addRow2(obj.name, obj.url, obj.isDir, obj.size, obj.sizeString, obj.dateModified, obj.dateModifiedString);
							}
						}
						//入栈
						if(indexName != null || indexName != undefined) {
							items.push(indexName); //入栈
							setFileTitleIndex();
							var box = document.getElementById("parentDirLinkBox");
							box.style.display = "block";
						}
					},
					//请求失败，包含具体的错误信息
					error: function(e) {
						console.log(e.status);
						console.log(e.responseText);
					}
				});

			}

			function addRow2(name, url, isdir,
				size, size_string, date_modified, date_modified_string) {
				if(name == "." || name == "..")
					return;

				var tbody = document.getElementById("tbody");
				var row = document.createElement("tr");
				var file_cell = document.createElement("td");
				var link = document.createElement("a");

				link.className = isdir ? "icon dir" : "icon file";

				if(isdir) {
					name = name + "/";
					url = url + "/";
					size = 0;
					size_string = "";
				} else {
					link.draggable = "true";
					link.addEventListener("dragstart", onDragStart, false);
				}
				link.innerText = name;
				link.href = "javascript:void(0)";
				link.onclick = function() {
					if(isdir) {
						initFileList(url, name);
					} else { //文件，弹出下载
						downloadFile(url);
					}
				}
				file_cell.dataset.value = name;
				file_cell.appendChild(link);

				row.appendChild(file_cell);
				row.appendChild(createCell(size, size_string));
				row.appendChild(createCell(date_modified, date_modified_string));

				tbody.appendChild(row);
			}
			/**
			 * 下载文件
			 * @param {Object} rootPath
			 */
			function downloadFile(filePath) {
				exportFile('form_download','file/download',filePath)
			}

			function exportFile(formId, url,filePath) {
				try {
					var queryForm = $("#" + formId);
					var exportForm = $("<form action='" + url + "' method='post'></form>")

					queryForm.find("input").each(function() {
						var name = $(this).attr("name");
						exportForm.append("<input type='hidden' name='" + name + "' value='" + filePath + "'/>")
					});


					$(document.body).append(exportForm);
					exportForm.submit();
				} catch(e) {
					console.log(e);
				} finally {
					exportForm.remove();
				}
			}


});