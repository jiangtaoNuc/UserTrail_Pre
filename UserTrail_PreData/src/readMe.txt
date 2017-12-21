1.读取3gsgn数据
	路径：112主机  /dfs/data/sgnftp/a1
	文件名：shlt-1-31382-20171219170500.txt
	大小：260M
	是否压缩：否
  读取4gsgn数据
  	路径：112主机 /dfs/data/4gsgnftp
  	文件名：S1MME20171219171700.txt.gz
  	大小：200M
  	是否压缩：是
  	
2.流程
	shell脚本调用java -jar 运行程序，文件名作为参数传入
	java程序读取.txt文件，截取所需要的字段，将用户号码，开始时间（秒），结束时间（秒），基站id（转换）
	记录处理的文件名，将文件名记录到一个文档里
	日志输出记录处理的条数
	
Select case when 
	trim(eventid) in('3','5','7','8','21','22','23','24','26') 
	then callednum 
	else callingnum end msisdn,changeover(btime) start_time,
	changeover(etime) end_time,b.bts_id bts_id,b.bts_name bts_name 
	from temp_sgn_3g a join ai_position_business.dim_bts_info b 
	on dealcell(a.cell)=b.bts join ssa.t_source_bscnid c
	 on (a.bsccode = c.bsc and c.nid=b.nid);"

bts_btsId_relation map(key:bts,nid;value:bts_id)
nid_bsc map(key:bscode;value:nid)
first:先通过bscode找nid_bsc中的nid，然后通过bts,nid找bts_btsId_relation中的bts_id
second：
	 
Select msisdn,procedure_start_time start_time,procedure_end_time end_time,
b.bts_id,b.bts_name from temp_sgn_4g
 a join ai_position_business.dim_bts_info_34g b on a.enb_id=b.bts_id;"