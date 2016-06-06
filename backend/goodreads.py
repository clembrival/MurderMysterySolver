from scrapy.crawler import CrawlerProcess
import scrapy

from sys import argv
import re


# Item class which stores the extracted data
class GoodreadsItem(scrapy.Item):
	title = scrapy.Field()
	avgRatings = scrapy.Field()
	numRatings = scrapy.Field()


# Spider class which extracts the data
class GoodreadsSpider(scrapy.Spider):
	name = "goodreads"	
	allowed_domains = ["goodreads.com"]
	start_urls = []
	output = []

	# Adds the url of the book's page to the list of pages to process
	def add_book(self, title):
		self.start_urls.append('http://www.goodreads.com/book/title?id="%s"' % title)

	# Extracts data from the urls stored in start_urls
	def parse(self, response):
		item = GoodreadsItem()
		item['title'] = response.xpath('//meta[@property="og:title"]/@content').extract()
		item['avgRatings'] = response.xpath('//span[@itemprop="ratingValue"]/text()').extract()
		item['numRatings'] = response.xpath('//span[@itemprop="ratingCount"]/text()').extract()
		
		book_info = []

		# Getting rid of any extra information in the title of the book
		reg_exp = re.compile("(.*?)\s*\(")
		extra_found = reg_exp.match(item['title'][0])
		
		if(extra_found):
			book_info.append(extra_found.group(1))
		else:
			book_info.append(item['title'][0])

		book_info.append(str(float(item['avgRatings'][0])))
		book_info.append(str(int((item['numRatings'][0].split(" Ratings")[0]).replace(',',''))))
		self.output.append(book_info)


# Creating the web spider
spider = GoodreadsSpider()


# Reading the title of the books from the file
# and adding the corresponding goodreads page to the spider
script, filename = argv
reader = open(filename)

for title in reader:
	spider.add_book(title)

reader.close()


# Starting the scraping process
crawler = CrawlerProcess({
	'USER_AGENT': 'Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)'
}) 
crawler.crawl(spider)
crawler.start()


# Outputting the data to a file
writer = open("output.txt", 'w')
for book in spider.output:
	writer.write(','.join(book) + '\n')
writer.close()