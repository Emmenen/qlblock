## 数据库指令
### 选择数据库
use databaseName
### 创建数据库
create databaseName
### 删除数据库
drop databaseName
### 增加/更改/删除/查询
insert/update/delete/select databaseName [key/regex] [value] [limit] [offset] [size]
#### 新增一条记录
insert testDatabase testKey testValue
#### 更改一条记录
update testDatabase testKey testValue
#### 删除一条记录
delete testDatabase testKey
#### 查询记录
select testDatabase testKey
#### 查询所有记录
select testDatabase * limit [0] [1]