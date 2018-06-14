package com.wangjikai;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;
import com.wangjikai.jd.JDShoping;
import com.wangjikai.listener.MyApplicationEnvironmentPrepareEventListener;
import com.wangjikai.po.*;
import com.wangjikai.util.JsoupUtils;
import com.wangjikai.util.QiniuUtils;
import com.wangjikai.vo.NovelVO;
import org.apache.commons.io.FileUtils;
import org.quartz.impl.StdScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.HtmlUtils;
import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import static java.lang.System.in;
import static java.lang.System.out;

@Controller
@SpringBootApplication
@ComponentScan(basePackages={"com.wangjikai"})
@EnableJpaRepositories(basePackages = "com.wangjikai")
@EntityScan(basePackages = "com.wangjikai.po")
public class SpringbootdemoApplication {

    private static String filePath = "E:/test";
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private Novel17KCrawler novel17KCrawler;

    @Resource
    private NovelLingDianCrawler novelLingDianCrawler;

    @Resource
    private AllNovelCrawler allNovelCrawler;

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private NovelRepository novelRepository;

    @Resource
    private NovelTypeRepository novelTypeRepository;

    @Resource
    private WebsiteRepository websiteRepository;

	public static void main(String[] args) {
        SpringApplication application = new SpringApplication(SpringbootdemoApplication.class);
	    application.addListeners(new MyApplicationEnvironmentPrepareEventListener());
		application.run(args);
	}

    @RequestMapping(value = "/index_v21.html")
    @ResponseBody
    public Properties host(){
        Properties props = System.getProperties();
        out.println("Java的运行环境版本：" + props.getProperty("java.version"));
        out.println("Java的运行环境供应商：" + props.getProperty("java.vendor"));
        out.println("Java供应商的URL："+ props.getProperty("java.vendor.url"));
        out.println("Java的安装路径：" + props.getProperty("java.home"));
        out.println("Java的虚拟机规范版本："+ props.getProperty("java.vm.specification.version"));
        out.println("Java的虚拟机规范供应商：" + props.getProperty("java.vm.specification.vendor"));
        out.println("Java的虚拟机规范名称："+ props.getProperty("java.vm.specification.name"));
        out.println("Java的虚拟机实现版本："+ props.getProperty("java.vm.version"));
        out.println("Java的虚拟机实现供应商：" + props.getProperty("java.vm.vendor"));
        out.println("Java的虚拟机实现名称：" + props.getProperty("java.vm.name"));
        out.println("Java运行时环境规范版本：" + props.getProperty("java.specification.version"));
        out.println("Java运行时环境规范供应商："+ props.getProperty("java.specification.vender"));
        out.println("Java运行时环境规范名称：" + props.getProperty("java.specification.name"));
        out.println("Java的类格式版本号："+ props.getProperty("java.class.version"));
        out.println("Java的类路径：" + props.getProperty("java.class.path"));
        out.println("加载库时搜索的路径列表："   + props.getProperty("java.library.path"));
        out.println("默认的临时文件路径：" + props.getProperty("java.io.tmpdir"));
        out.println("一个或多个扩展目录的路径：" + props.getProperty("java.ext.dirs"));
        out.println("操作系统的名称：" + props.getProperty("os.name"));
        out.println("操作系统的构架：" + props.getProperty("os.arch"));
        out.println("操作系统的版本：" + props.getProperty("os.version"));
        out.println("文件分隔符：" + props.getProperty("file.separator"));  //在 unix 系统中是＂／＂
        out.println("路径分隔符：" + props.getProperty("path.separator")); // 在 unix 系统中是＂:＂
        out.println("行分隔符：" + props.getProperty("line.separator")); // 在 unix系统中是＂/n＂
        out.println("用户的账户名称：" + props.getProperty("user.name"));
        out.println("用户的主目录：" + props.getProperty("user.home"));
        out.println("用户的当前工作目录：" + props.getProperty("user.dir"));
        return props;
    }

    @RequestMapping(value = "/index_v31.html")
    @ResponseBody
    public Map<String, String> hostIp(){
        Map<String, String> map = System.getenv();
        String userName = map.get("USERNAME");// 获取用户名
        String computerName = map.get("COMPUTERNAME");// 获取计算机名
        String userDomain = map.get("USERDOMAIN");// 获取计算机域名
        out.println(userName);
        out.println(computerName);
        out.println(userDomain);
        return map;
    }
    @RequestMapping(value = "/graph_echarts1.html")
    @ResponseBody
    public Properties hostIp5(){
        Properties p=System.getProperties();//获取当前的系统属性
        p.list(out);//将属性列表输出
        out.print("CPU个数:");//Runtime.getRuntime()获取当前运行时的实例
        out.println(Runtime.getRuntime().availableProcessors());//availableProcessors()获取当前电脑CPU数量
        out.print("虚拟机内存总量:");
        out.println(Runtime.getRuntime().totalMemory());//totalMemory()获取java虚拟机中的内存总量
        out.print("虚拟机空闲内存量:");
        out.println(Runtime.getRuntime().freeMemory());//freeMemory()获取java虚拟机中的空闲内存量
        out.print("虚拟机使用最大内存量:");
        out.println(Runtime.getRuntime().maxMemory());//maxMemory()获取java虚拟机试图使用的最大内存量
        return p;
    }

    //文件下载
    //chrome : Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36
    //IE : Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko
    //Edge : Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 Edge/16.16299
    @RequestMapping(value = "/download")
    public ResponseEntity<byte[]> download(HttpServletRequest request){
        String filename = "手机是三鸡-逆天仙尊.txt";
        File file = new File(filePath+File.separator+filename);
        HttpHeaders headers = new HttpHeaders();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String downloadFielName = format.format(new Date())+"手机是三鸡-逆天仙尊.txt";
        String agent = request.getHeader("USER-AGENT");
        String name = "";
        try {
            if (null != agent && (agent.contains("Edge"))) {
                name = URLEncoder.encode(downloadFielName, "UTF8");
                //将加号还原为空格
                downloadFielName = name.replaceAll("\\+", "%20");
            } else if (agent.contains("Safari") || agent.contains("Chrome") || agent.contains("Firefox")) {
                downloadFielName = new String(downloadFielName.getBytes("UTF-8"), "iso-8859-1");
            } else { // IE
                name = URLEncoder.encode(downloadFielName, "UTF-8");
                //将加号还原为空格
                downloadFielName = name.replaceAll("\\+", "%20");
            }
        } catch (UnsupportedEncodingException e) {
            downloadFielName = "file";
            out.println(filename+"字符转换错误，已使用默认名");
        }
        headers.setContentDispositionFormData("attachment",downloadFielName);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        try {
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file),headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<byte[]>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 文件上传，注意多文件情况
     */
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public void index(@RequestParam(value = "file",required = false) MultipartFile file, HttpServletResponse response) throws IOException {
        if (file != null) {
            file.transferTo(new File(filePath,file.getOriginalFilename()));
        }
    }

    /**
     * 将Base64位编码的图片进行解码，并保存到指定目录
     */
    @RequestMapping(path = "/uploadHead", method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> decodeBase64DataURLToImage(String dataURL) throws Exception {
        // 将dataURL开头的非base64字符删除
        String base64 = dataURL.substring(dataURL.indexOf(",") + 1);
        String key = new QiniuUtils().put64image(base64);
        String imageUrl = "http://p27w5ko62.bkt.clouddn.com/"+key;
        FileOutputStream write = new FileOutputStream(new File(filePath+File.separator+"头像.png"));
        byte[] decoderBytes = Base64.getDecoder().decode(base64);
        write.write(decoderBytes);
        write.close();
        Map<String,Object> map = new HashMap<>();
        map.put("url",imageUrl);
        return map;
    }

    /**
     * 漏洞测试
     * 前台取得cookie
     */
    @RequestMapping(path = "/hacker", method = RequestMethod.GET)
    public void getsda(HttpServletRequest request,HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            out.println(cookie.getName());
            out.println(cookie.getValue());
            out.println(cookie.getVersion());
        }
        Cookie cookie = new Cookie("wjk","car shop");
        //单位是秒
        cookie.setMaxAge(60*60);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    @RequestMapping(value = "/putValue",method = RequestMethod.POST)
    @ResponseBody
    public String putDemo(HttpServletRequest request) {
        String string = request.getParameter("aaa");
        //用户输入的地方进行检测脱敏，保证表单的标点符号正确编码
        //富文本上传漏洞，通过jsoup白名单解决
        //白名单StringEscapeUtils.escapeHtml4(string);
        String bbb = HtmlUtils.htmlEscape(string);
        return bbb;
    }

    @RequestMapping(value = "/putCommons",method = RequestMethod.POST)
    @ResponseBody
    public String putCommons(HttpServletRequest request, Model model) {
        //也就不是上班多辛苦了,天底下都一样,挣钱的都要干活.
        String content = request.getParameter("editorValue");
        String safeContent = "";
        if (content != null) {
            safeContent = JsoupUtils.cleanUserInputHTMLContent(content);
            out.println("安全字符："+safeContent);
        }
        return safeContent;
    }

    @RequestMapping(value = "ddd")
    public String ddd(@RequestParam(value = "max",defaultValue = "20") long max) {
        return "redirect:http://baidu.com/";
    }

    @RequestMapping(value = "novel")
    public String novel() {
        return "index";
    }

    @RequestMapping(value = "/cookie")
    @ResponseBody
    public void cookie(HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        PrintWriter out=response.getWriter();
        JDShoping shoping = new JDShoping();
        StringBuffer stringBuffer = new StringBuffer();
        List<String> list = shoping.doLogin();
        for (String s : list) {
            stringBuffer.append("<li>"+s+"</li>");
        }
        String html = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "</head>\n" +
                "<body>\n" +
                "<ul>"+stringBuffer+"</ul>\n" +
                "</body>\n" +
                "</html>";
        out.println(html);
    }

    @RequestMapping(value = "/17k")
    @ResponseBody
    public void dsda(HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        PrintWriter out=response.getWriter();
        //Novel17KCrawler novel17KCrawler = new Novel17KCrawler();
        StringBuffer stringBuffer = new StringBuffer();
        //List<String> list = novel17KCrawler.aaa();
        List<String> list = new ArrayList<>();
        for (String s : list) {
            stringBuffer.append("<li><span>"+s+"</span></li>");
        }
        String html = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "</head>\n" +
                "<body>\n" +
                "<ul>"+stringBuffer+"</ul>\n" +
                "</body>\n" +
                "</html>";
        out.println(html);
    }

    /**
     * @GetMapping(value = "vvv") 组合注解，spring4.3开始支持
     */
    @RequestMapping(value = "vvv")
    public String vvv(@Validated POJO pojo, BindingResult result) {
        //System.out.println(result.getFieldError().getDefaultMessage());
        if (result.hasErrors()) {
            return "redirect:https://www.csdn.net/";
        }
        return "vueTest";
    }

    @GetMapping(value = "/pay")
    public void value(HttpServletResponse response) throws AlipayApiException, IOException {
        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipaydev.com/gateway.do",
                "2016082700322292", "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCUVESbhsBqqYuP4dAZUxOF+p6hjVknMMvDbvS4IBbH/Q4iOO62ueTxzQur2QqTE8USGxzJbhZBgHG4GFXHR2amPQ3zEAi6XkdKq8nyuW6ENhIt+ci6n6z6R2poOMBPlOy4G6TVqdbi9hffb/vKSzjmW6vogfIGXVSA+wR+ehmqSIGZqT6MtwnOeAaEklq1APLeOIhOCuAdLyTYe7E3ypQsaz1g06dDJPKRzVBk9Ed6TIGkd4DmnP6XRfd5WnTgUkUAIwgWb+6cE52lvV8ovketZkN0MYw7cmt+1UkrlQRXvBiHbqF2x9ZEh+BGmueVPk0rE29B3dz+u4GbTQjLLUgpAgMBAAECggEBAIPQXyfXGCexg9TZV5Upvq/RiCcI9bFeK7YBZHg8DAKtX2IKNhksGcjH7xdISZ1qdDci4PIgYqbS/jwbZB8z1ITmvP7DwP3dyn0RLOYG46+aXY197GPsgkvoKydzR3NxZOw3pMQ7qjsHdy+Y1KlxL5+G175HvEzro6z7ZYL//s7TQw9U3EaaTqmePNHtFa2DCOxOEnap9+Ypkw09jBhQTi02hNOPzOu3sl5QFcp3Q9kLkzvuRd8InVlwrbWmeJHdKs9susAGAc1FA6EjtYeYhydFo4vOIoA+lz0P9pPPsWLUe3ewgMqlFeBiLpBwoxJ+1TIaTH6GPbdpD/wJgOzVgAECgYEA3f0/IVUxUA0vpnrNYENN5T8BrPZ4/OZEQ+gT/bXydjkPJBsI3HoYTYRs5ClHsWN0w1PlPVopQm7wBSrEAJmoAhJ3uz8DjoiUNnUKQO7kLt4bVVDzJODAQ6hVyGiraHwU69SN0My+ztISo9ERXdVNYc+V/+sEPABI3qYpRORdiMkCgYEAqw32TOubkzJ075SOgnxH6a+2BKzTOupAbeuMuVGdJva3BqlJzi5Nx0bBP6sE24DVBFSA0G5JjZcdFrW+MsMxlyDEAvB1FpE3l7DO3nUl/PXG8bfXzNjc0BXnXShw5nd6T89hISSGEq0B1G4V+JNX/SWMod4VAO8WDNoFZWvG1GECgYEAo0j6PLx7b1SjriM6Ggqnq25y/xS03eFKaWC2A7Lny8ogtwAjvnkYkzxLFHDAyVxMsLhvMHe3TaRobISOc5qSmCOZamPpa9hOnNfuWODfexHorRLEJmYjRiD2KFnFay+J/AX24pUX7O1cxJ6t1YVmTAHzZbErBi93aj5ysLgBjPECgYBExadKA8wX4UhsbpDhW3BaPv/yXz3JyRbAWtzIQUzqtnSdpAB9edZhkUvu+iZIzkJWrFbh9gVyfNgAIm/m3t2YQl8sQ1sACMCOJ1L006rDSlmDI4QK4wHcU4IizTifIFg3sEv/1DxnU9GRg/UnbK4KpEcnaS+OoZF+90UGIs/c4QKBgQDRN2/uD5KEFFUunsHVb9go8rmx3CIKJVTWV8JG9ibz7uWXAzz9HgPlLcNMA0MJLc3nvuNmaxNq20Sakzb1wn0Kikke/70bnItyTLLsUnmNraT1AKzgHHsROB+uefEtpFbmc7ZXdQnXzPMwP7dMqEjbIbGtvxE1QDgBijDnX6sTvQ==",
                "json", "utf-8", "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAv203VADOwSl3+Wt29a86citfeKFhlxCj/klv99ikwcCiBHh35hnFMNOM2kGaeLeCmeA8HXo25aJLvgjQNm9t/CVlHqzOmYE+i9p+J2R5HunAEdh1SfFiPcri3ZZemZDhKSN6emARh66VHB19zWts9JIEeRKwod7r6QCD4rwPs8g2Ouzi1SnDPpu4VmYdaGX5e3bxiAUuR/peHV+wnOAz1LPFQb8l2WAWR8miQWMaqfdTUmi1Uxoj65SOqiNfOP/4dk1q/2TgSTUdjarZlNMoIDrp8XxtJzW+VppPMAJh88cLw8YxMsf5MC16yENhvnHhqayj11oXXzphst9C6YPK7wIDAQAB", "RSA2");
        //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl("http://localhost:8080/alipay.trade.page.pay-JAVA-UTF-8/return_url.jsp");
        alipayRequest.setNotifyUrl("http://localhost:8080/alipay.trade.page.pay-JAVA-UTF-8/notify_url.jsp");
        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = UUID.randomUUID().toString();
        //付款金额，必填
        String total_amount = "89.00";
        //订单名称，必填
        String subject = "JAVA 编程思想";
        //商品描述，可空
        String body = "";

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
        String result = alipayClient.pageExecute(alipayRequest).getBody();
        response.setContentType("text/html;charset=utf-8");
        PrintWriter out=response.getWriter();
        out.println(result);
    }

    /**
     * 对于用户来说，不知道会输入什么数据，所以可能造成类型转换异常
     * 统一使用字符串进行接受，存在异常时直接返回
     * 防止json请求者引起系统崩溃
     */
    @RequestMapping(value = "/wjk/{spittleId}", method = RequestMethod.GET)
    public String showSpittle(@PathVariable String spittleId) {
        try {
            Integer.valueOf(spittleId);
        } catch (NumberFormatException e) {
            return "";
        }
        Integer.valueOf(spittleId);
        out.println(spittleId);
        return "spittle";
    }


    @RequestMapping(value = "/")
    public String index(Model model) {
        //为了避免NULL错误
        Novel novel = novelRepository.findById(1266).orElse(new Novel());
        model.addAttribute("novel",novel);
        Novel novel1 = novelRepository.findById(3235).orElse(new Novel());
        Novel novel2 = novelRepository.findById(4235).orElse(new Novel());
        Novel novel3 = novelRepository.findById(5235).orElse(new Novel());
        Novel novel4 = novelRepository.findById(6235).orElse(new Novel());
        Novel novel5 = novelRepository.findById(7235).orElse(new Novel());
        Novel novel6 = novelRepository.findById(8235).orElse(new Novel());
        Novel novel7 = novelRepository.findById(9235).orElse(new Novel());
        Novel novel8 = novelRepository.findById(19235).orElse(new Novel());
        Novel novel9 = novelRepository.findById(9135).orElse(new Novel());
        Novel novel10 = novelRepository.findById(2915).orElse(new Novel());
        Novel novel11 = novelRepository.findById(3135).orElse(new Novel());
        Novel novel12 = novelRepository.findById(4913).orElse(new Novel());
        Novel novel13 = novelRepository.findById(5935).orElse(new Novel());
        Novel novel14 = novelRepository.findById(9136).orElse(new Novel());
        Novel novel15 = novelRepository.findById(1405).orElse(new Novel());
        List<Novel> novels = new ArrayList<>();
        novels.add(novel1);
        novels.add(novel2);
        novels.add(novel3);
        novels.add(novel4);
        novels.add(novel5);
        novels.add(novel6);
        novels.add(novel7);
        novels.add(novel8);
        novels.add(novel9);
        novels.add(novel10);
        novels.add(novel11);
        novels.add(novel12);
        novels.add(novel13);
        novels.add(novel14);
        novels.add(novel15);
        model.addAttribute("novels",novels);
        return "index";
    }

    @RequestMapping(value = "/book/{bookId}/{chapterId}", method = RequestMethod.GET)
    public String book(@PathVariable("bookId") String bookId,@PathVariable("chapterId") String chapterId,Model model) throws IOException {
        Query query = new Query();
        Criteria criteria = new Criteria("_id").is(chapterId);
        query.addCriteria(criteria);
        NovelContent novelContent = mongoTemplate.findOne(query,NovelContent.class);
        Novel novel = novelRepository.findById(Integer.valueOf(bookId)).get();
        if (StringUtils.isEmpty(novel) || StringUtils.isEmpty(novelContent)) {
            return "notfound";
        }
        model.addAttribute("content",novelContent);
        model.addAttribute("novel",novel);
        return "article";
    }

    @RequestMapping(value = "/booklist", method = RequestMethod.GET)
    public String rediectPath(Model model) throws Exception {
        return "notfound";
    }

    @RequestMapping(value = "/booklist/{id}", method = RequestMethod.GET)
    public String booklist(Model model,@PathVariable("id") String id) throws Exception {
        int intId;
        try {
            intId = Integer.valueOf(id);
        } catch (NumberFormatException e) {
            return "notfound";
        }
        Optional<Novel> novelOptional = novelRepository.findById(intId);
        if (!novelOptional.isPresent()) {
            return "notfound";
        }
        Novel novel = novelOptional.get();
        novelLingDianCrawler.setNovel(novel);
        //获取源网站
        Website website = websiteRepository.findById(novel.getWebSiteType()).orElse(new Website());

        //List<NovelUrl> novelUrls = novelLingDianCrawler.crawlerList(novelListCssQuery,charSet,novelTitleCssQuery,novelContentCssQuery);
        List<NovelUrl> novelUrls = novelLingDianCrawler.crawlerList(website);
        model.addAttribute("novelUrls",novelUrls);
        model.addAttribute("novel",novel);
        return "booklist";
    }

    /**
     * 获取每个类别下的小说页数，组装成list，爬取
     * 获取从起始页开始，当前起始页所在的类别的最大页数，以及当前起始页的类别名称
     */
    @RequestMapping(value = "/crawlerAll")
    public String crawlerAll(Model model) throws Exception {
        //ModelAndView model = new ModelAndView();
        //网站类型
        int webSiteType = 2;
        Website website = websiteRepository.findById(webSiteType).orElse(new Website());

        //bloomFilter
        Funnel<Novel> novelFunnel = new Funnel<Novel>() {
            @Override
            public void funnel(Novel novel, PrimitiveSink primitiveSink) {
                primitiveSink.putString(novel.getListUrl(), Charsets.UTF_8);
                primitiveSink.putString(novel.getName(),Charsets.UTF_8);
                primitiveSink.putString(novel.getAuthor(),Charsets.UTF_8);
            }
        };
        BloomFilter<Novel> bloomFilter = BloomFilter.create(novelFunnel,92000);

        //类型页种类数
        int typeNumber = website.getWebSiteTypeNumber();
        int pageSplitSize = website.getPageSplitSize();
        //网址主页
        String baseUrl = website.getWebSiteBaseUrl();
        //网址前缀
        String urlPrefix = website.getWebSiteUrlPrefix();
        //网址第一页后缀
        String firstPageSuffix = website.getWebSiteFirstPageSuffix();
        //网址中缀
        String urlInfix = website.getWebSiteUrlInfix();
        //网址后缀
        String urlSuffix = website.getWebSiteUrlSuffix();
        //网站编码
        String charSet = website.getWebSiteCharSet();
        //类型页面中【类型的页码数】元素解析
        String numberCssQuery = website.getWebSiteNumberCssQuery();
        //类型页面中【类型的名称】元素解析
        String typeNameCssQuery = website.getWebSiteTypeNameCssQuery();

        String novelCssQuery = website.getNovelCssQuery();
        String novelNameCssQuery = website.getNovelNameCssQuery();
        String novelListUrlCssQuery = website.getNovelListUrlCssQuery();
        String novelPicUrlCssQuery = website.getNovelPicUrlCssQuery();
        String novelAuthorCssQuery = website.getNovelAuthorCssQuery();
        String novelResumeCssQueryByType = website.getNovelResumeCssQueryByType();
        String novelResumeCssQueryByContent = website.getNovelResumeCssQueryByContent();

        //模拟用户输入的起始页,用户输入每个小说类型的起始页面，后台组装成list。
        List<NovelTypeStartPage> list = new ArrayList<>();
        for (int i = 1; i <= typeNumber; i++) {
            NovelTypeStartPage startPage = new NovelTypeStartPage();
            startPage.setWebSiteType(webSiteType);
            startPage.setBaseUrl(baseUrl);
            startPage.setUrlPrefix(urlPrefix);
            startPage.setUrlSuffix(urlSuffix);
            startPage.setUrlInfix(urlInfix);
            startPage.setStartPageUrl(baseUrl + urlPrefix + i + urlInfix + firstPageSuffix);
            startPage.setCharSet(charSet);
            startPage.setNumberCssQuery(numberCssQuery);
            startPage.setTypeNameCssQuery(typeNameCssQuery);
            startPage.setNovelCssQuery(novelCssQuery);
            startPage.setNovelNameCssQuery(novelNameCssQuery);
            startPage.setNovelListUrlCssQuery(novelListUrlCssQuery);
            startPage.setNovelPicUrlCssQuery(novelPicUrlCssQuery);
            startPage.setNovelAuthorCssQuery(novelAuthorCssQuery);
            startPage.setNovelResumeCssQueryByType(novelResumeCssQueryByType);
            startPage.setNovelResumeCssQueryByContent(novelResumeCssQueryByContent);
            list.add(startPage);
        }
        //判断是否完成的标志位
        Set<Boolean> done = new HashSet<>();
        List<Future<NovelTypeStartPage>> activeThread = allNovelCrawler.getFutureList();
        if (activeThread != null) {
            for (Future<NovelTypeStartPage> novelTypeStartPageFuture : activeThread) {
                //判断是否完成,未完成的添加到集合
                if (!novelTypeStartPageFuture.isDone()) {
                    done.add(false);
                }
            }
        }
        if (done.size() == 0) {
            //获取小说网站每种类型的总页数
            long startTime = System.currentTimeMillis();
            List<NovelTypeStartPage> startPages = allNovelCrawler.crawlerTypePage(list,website);
            List<NovelType> novelTypes = new ArrayList<>(startPages.size());
            for (NovelTypeStartPage startPage : startPages) {
                novelTypes.add(startPage.getNovelType());
            }
            novelTypeRepository.saveAll(novelTypes);
            long endTime = System.currentTimeMillis();
            System.out.println("获取各个类型的页数耗时："+(endTime-startTime)+"毫秒");
            List<Novel> novels = novelRepository.findAll();
            for (int i = 0; i < novels.size(); i++) {
                bloomFilter.put(novels.get(i));
            }
            //设置分组数
            allNovelCrawler.setSplitSize(pageSplitSize);
            startTime = System.currentTimeMillis();
            List<NovelTypeStartPage> novelTypeStartPageList = allNovelCrawler.crawlerAllNovelByType(list);
            endTime = System.currentTimeMillis();
            long time = endTime - startTime;
            System.out.println("爬取所有类型下的所有页数下的所有书目耗时："+time+"毫秒");
            model.addAttribute("startPages",startPages);
            model.addAttribute("time",time);
            //model.addObject("startPages",startPages);
            //model.addObject("time",time);
            //设置运行标志位false，标志运行结束
            allNovelCrawler.setRunFlag(false);
            List<Novel> novelList = new LinkedList<>();
            List<Novel> novelToSave = new LinkedList<>();
            //入库
            for (NovelTypeStartPage novelTypeStartPage : novelTypeStartPageList) {
                novelList.addAll(novelTypeStartPage.getNovelList());
            }
            for (Novel novel : novelList) {
                if (!bloomFilter.mightContain(novel)) {
                    novelToSave.add(novel);
                }
            }
            novelRepository.saveAll(novelToSave);
        }
        return "crawler";
    }

    /**
     * 请求爬取进度页面，进行页面的爬取监控
     */
    @GetMapping(value = "/progress")
    public String progress() {
        return "progress";
    }

    /**
     * 爬取监控页面实际请求地址，获取数据
     */
    @PostMapping(value = "/getProgress")
    @ResponseBody
    public Map<String,Object> getProgress() {
        List<GetNovelUrlByTypeThread> getNovelUrlByTypeThreadList = allNovelCrawler.getGetNovelUrlByTypeThreads();
        Map<String,Object> map = new HashMap<>(16);
        List<Progress> progressList = new ArrayList<>();
        map.put("progressList",progressList);
        map.put("stop",!allNovelCrawler.isRunFlag());
        Set<Boolean> booleans = new HashSet<>();
        if (getNovelUrlByTypeThreadList != null) {
            for (GetNovelUrlByTypeThread getNovelUrlByTypeThread : getNovelUrlByTypeThreadList) {
                Progress progress = new Progress();
                int currentProgress = getNovelUrlByTypeThread.getProgress();
                int size = getNovelUrlByTypeThread.getNovelTypeStartPage().getAllUrls().size();
                progress.setStartPageUrl(getNovelUrlByTypeThread.getNovelTypeStartPage().getStartPageUrl());
                progress.setSize(size);
                progress.setCurrentProgress(currentProgress);
                if (currentProgress == size) {
                    booleans.add(true);
                } else {
                    booleans.add(false);
                }
                progressList.add(progress);
            }
        } else {
            map.put("stop",true);
        }
        if (booleans.size() == 1) {
            map.put("stop",true);
        }
        map.put("stop",!allNovelCrawler.isRunFlag());
        return map;
    }

    @GetMapping(value = "/admin")
    public String adminIndexPage() {
        return "admin";
    }

    @GetMapping(value = "/number")
    @ResponseBody
    public long getNumber() {
        return novelRepository.count();
    }

    @GetMapping(value = "/types")
    @ResponseBody
    public Page<NovelType> getTypes() {
        Pageable pageable = PageRequest.of(0, 6);
        Page<NovelType> novelTypes = novelTypeRepository.findAll(pageable);
        NovelType novelType = new NovelType();
        novelType.setTypeName("");
        Example<NovelType> novelTypeExample = Example.of(novelType);
        novelTypeRepository.findAll(novelTypeExample);
        return novelTypes;
    }

    @GetMapping(value = "/searchBook")
    @ResponseBody
    public List<NovelVO> searchBook(@RequestParam(value = "w",required = false) String word) {
        //搜索结果会有多个，暂时需要解决如何显示多本重复的显示界面
        //暴露的问题，查询会慢，后期采用ES进行处理
        List<Novel> novels = novelRepository.findNovelsByNameContainingOrAuthorContaining(word, word);
        List<NovelVO> novelVOS = new ArrayList<>(novels.size());
        for (Novel novel : novels) {
            NovelVO novelVO = new NovelVO();
            novelVO.setAuthor(novel.getAuthor());
            novelVO.setName(novel.getName());
            novelVO.setPicUrl(novel.getPicUrl());
            novelVO.setResume(novel.getResume());
            novelVO.setTypeName(novel.getNovelType().getTypeName());
            novelVOS.add(novelVO);
        }
        return novelVOS;
    }
}
