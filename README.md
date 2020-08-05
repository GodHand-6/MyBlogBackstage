个人博客后端

采用 springboot + mysql + ES

文档存储在MySQL中，es存储的是展示数据和能从数据库取出数据的key。

ES索引结构：
<br>
&nbsp;&nbsp;doc_id 数据库的表主键
<br>
&nbsp;&nbsp;doc_name 文档名字，用 ik 分词器分词用来做搜索。 
<br>
&nbsp;&nbsp;doc_type 文档类型（对应数据库名的前缀），没有考虑多类型的情况
<br>
&nbsp;&nbsp;doc_date 文档上传的时间

数据库结构：
<br>
&nbsp;&nbsp;id 主键
<br>
&nbsp;&nbsp;doc 文档内容
<br>
&nbsp;&nbsp;date 文档上传的时间

由于一开始忘了考虑标签功能，只考虑了分类，为了取出某一类的数据的效率， 就将某一类数据存储到固定的库中。
将类别分为了四类，Environment、MyDate、Record、Study四类，练手项目，因为没有做上传和管理文件的后台
所以类别数据都是写的静态的，在utils包下。

文件上传：<br>
用的是扫描本地文件的方式，可以在resources文件夹下创建blog文件夹，然后再创建上面的四个类的文件夹，
将文件分别放入文件夹内，在test下的测试类中有创建索引和扫描文件到数据库和es中的测试代码。
